package com.pettact.api.code.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CommonCode {
	@Id
	private String codeId;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_code", nullable = false)
    private GroupCode group;

    @Column(nullable = false)
    private String codeName;

    @Column(nullable = false)
    private Integer codeOrder;

    @Column
    private String description;
}
