package de.balvi.cuba.sessioninfo.gui.session

import javax.annotation.Nullable

enum PermissionValueEnum {
    YES(1, 'font-icon:CHECK_SQUARE_O'),
    NO(0, 'font-icon:SQUARE_O')

    Integer id
    String icon

    PermissionValueEnum(Integer value, String icon) {
        this.id = value
        this.icon = icon
    }

    @Nullable
    static PermissionValueEnum fromId(Integer id) {
        for (PermissionValueEnum at : values()) {
            if (at.id == id) {
                return at
            }
        }
        null
    }
}