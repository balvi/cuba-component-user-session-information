package de.balvi.cuba.sessioninfo.gui.session

import javax.annotation.Nullable

enum PermissionEntityAttributeValueEnum {
    MODIFY(2, 'icons/edit.png'),
    READ_ONLY(1, 'icons/eye-plus.png'),
    HIDE(0, 'icons/eye-minus.png')

    Integer id
    String icon

    PermissionEntityAttributeValueEnum(Integer value, String icon) {
        this.id = value
        this.icon = icon
    }

    @Nullable
    static PermissionEntityAttributeValueEnum fromId(Integer id) {
        for (PermissionEntityAttributeValueEnum at : values()) {
            if (at.id == id) {
                return at
            }
        }
        null
    }
}