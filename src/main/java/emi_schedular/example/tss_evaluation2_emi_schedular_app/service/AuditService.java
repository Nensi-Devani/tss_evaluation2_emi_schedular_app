package emi_schedular.example.tss_evaluation2_emi_schedular_app.service;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.AuditAction;

// records every important loan-engine event (applied, approved, rejected, paid, ...)
public interface AuditService {
    void log(User actor, AuditAction action, String targetEntity, Long targetId);
}
