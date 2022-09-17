package com.fentric.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreeSelectMenu {
    private Long id;
    //此id不用传递给前端
    private Long fatherId;
    private String label;
    private List<TreeSelectMenu> children;
}
