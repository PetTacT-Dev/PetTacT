package com.pettact.api.code.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class GroupCode {

	@Id
	private String groupCode;

    @Column(nullable = false)
    private String groupName;

    @Column
    private String description;
}
