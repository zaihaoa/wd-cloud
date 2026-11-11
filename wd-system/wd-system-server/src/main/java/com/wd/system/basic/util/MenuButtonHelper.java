package com.wd.system.basic.util;

import com.wd.system.basic.domain.model.MenuButton;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class MenuButtonHelper {

    private MenuButtonHelper() {
    }

    public static Stream<String> permissionCodes(List<MenuButton> buttons) {
        if (buttons == null) {
            return Stream.empty();
        }
        return buttons.stream()
                .filter(Objects::nonNull)
                .map(MenuButton::getCode)
                .filter(code -> code != null && !code.isBlank() && code.equals(code.strip())
                        && code.length() <= 200 && !code.contains("*"));
    }
}
