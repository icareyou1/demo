package com.fentric.domain.requestVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleAddOrUpdateParams {
    private Long roleId;
    private String roleName;
    private String status;
    private String comment;
    private List<Long> menuIds;
}
