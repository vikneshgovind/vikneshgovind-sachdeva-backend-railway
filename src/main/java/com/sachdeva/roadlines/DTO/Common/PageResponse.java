package com.sachdeva.roadlines.DTO.Common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
 private List<T> content;
 private long totalElements;
 private int totalPages;
 private int pageNumber;
 private int pageSize;
}

