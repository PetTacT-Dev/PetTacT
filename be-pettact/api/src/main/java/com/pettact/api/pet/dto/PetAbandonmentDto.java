package com.pettact.api.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetAbandonmentDto {
	private String noticeNo;
	private String sprtEDate;
	private String desertionNo;
	private String rfidCd;
	private String happenDt;
	private String happenPlace;
	private String kindCd;
	private String colorCd;
	private String age;
	private String weight;
	private String evntImg;
	private String updTm;
	private String endReason;
	private String careRegNo;
	private String noticeSdt;
	private String noticeEdt;
	private String processState;
	private String sexCd;
	private String neuterYn;
	private String specialMark;
	private String careNm;
	private String careTel;
	private String careAddr;
	private String orgNm;
	private String sfeSoci;
	private String sfeHealth;
	private String etcBigo;
	private String kindFullNm;
	private String upKindCd;
	private String upKindNm;
	private String kindNm;
	private String popfile1;
	private String popfile2;
	private String popfile3;
	private String popfile4;
	private String popfile5;
	private String popfile6;
	private String popfile7;
	private String popfile8;
	private String careOwnerNm;
	private String vaccinationChk;
	private String healthChk;
	private String adptnTitle;
	private String adptnSDate;
	private String adptnEDate;
	private String adptnConditionLimitTxt;
	private String adptnTxt;
	private String adptnImg;
	private String sprtTitle;
	private String sprtSDate;
	private String sprtConditionLimitTxt;
	private String sprtTxt;
	private String sprtImg;
	private String srvcTitle;
	private String srvcSDate;
	private String srvcEDate;
	private String srvcConditionLimitTxt;
	private String srvcTxt;
	private String srvcImg;
	private String evntTitle;
	private String evntSDate;
	private String evntEDate;
	private String evntConditionLimitTxt;
	private String evntTxt;
	private Integer petViewCnt;
}
