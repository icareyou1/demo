package com.fentric.domain.requestVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagQueryParams {
    private Long pageNum;
    private Long pageSize;
    private String tagName;
    private String status;
    //装载时间
    private String beginTime;
    private String endTime;
}
