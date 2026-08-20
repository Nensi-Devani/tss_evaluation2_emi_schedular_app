package emi_schedular.example.tss_evaluation2_emi_schedular_app.service.implementation;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.common.PageDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.dto.response.EmiResponseDto;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.Emi;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.EmiStatus;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.exception.ResourceNotFoundException;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.mapper.EmiMapper;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.EmiRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.repository.LoanRepository;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.BorrowerEmiService;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class BorrowerEmiServiceImpl implements BorrowerEmiService {

    private final EmiRepository emiRepository;
    private final EmiMapper emiMapper;
    private final LoanRepository loanRepository;
    private final SecurityService securityService;

    @Override
    @Transactional(readOnly = true)
    public PageDto<EmiResponseDto> getLoanEmis(Long loanId, EmiStatus status, Pageable pageable) {
        User currentUser = securityService.getCurrentUser();

        boolean ownsLoan = loanRepository.existsByIdAndBorrowerId(loanId, currentUser.getId());

        if (!ownsLoan) {
            throw new ResourceNotFoundException("Loan not found");
        }

        log.info(
                "Fetching borrower EMIs. loanId={}, status={}, userId={}, page={}, size={}",
                loanId,
                status,
                currentUser.getId(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<Emi> emiPage = emiRepository.findByLoanIdAndStatus(loanId, status, pageable);

        return new PageDto<>(
                emiPage,
                emiMapper::toResponseDto
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EmiResponseDto getNextUpcomingEmi(Long loanId) {
        User currentUser = securityService.getCurrentUser();

        boolean ownsLoan = loanRepository.existsByIdAndBorrowerId(loanId, currentUser.getId());

        if (!ownsLoan) {
            throw new ResourceNotFoundException("Loan not found");
        }

        log.info(
                "Fetching next upcoming EMI. loanId={}, userId={}",
                loanId,
                currentUser.getId()
        );

        Emi emi = emiRepository
                .findFirstByLoanIdAndStatusAndDueDateGreaterThanEqualOrderByDueDateAsc(
                        loanId,
                        EmiStatus.PENDING,
                        LocalDate.now()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Your loan is closed. No further EMI payments are due."
                        )
                );

        return emiMapper.toResponseDto(emi);
    }
}
