package com.fentric.domain.requestVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserQueryParams {
    private Long pageNum;
    private Long pageSize;
    private String userName;
    private String phoneNumber;
    private String gender;
    private String status;
    private Long orgId;
    //里面装载时间
    private String beginTime;
    private String endTime;
}
