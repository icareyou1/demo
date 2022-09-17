package com.fentric.domain.requestVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleQueryParams {
    private Long pageNum;
    private Long pageSize;
    private String roleName;
    private String status;
    private String params;
}
