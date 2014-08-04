package com.alex.yyf.spider.domain.drug;

import java.util.List;

import lombok.Data;

@Data
public class DrugListDto {

	private boolean success;
	private int total;
	private List<Drug> yi18;
}
