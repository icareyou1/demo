package com.fentric.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreeSelectOrg {
    private Long orgId;
    private Long parentId;
    private String orgName;
    private List<TreeSelectOrg> children;
}
