package com.fentric.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouterMenu {
    private String name;
    private String path;
    private boolean hidden;
    private String component;
    private RouterMetaMenu meta;
    private List<RouterMenu> children;
}
