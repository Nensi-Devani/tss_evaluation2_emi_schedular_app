package emi_schedular.example.tss_evaluation2_emi_schedular_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "system_configurations")
@Getter
@Setter
@NoArgsConstructor
public class SystemConfiguration extends BaseEntity{

    @Column(
            name = "config_key",
            nullable = false,
            unique = true,
            length = 100
    )
    private String configKey;

    @Column(
            name = "config_value",
            nullable = false,
            length = 500
    )
    private String configValue;
}
