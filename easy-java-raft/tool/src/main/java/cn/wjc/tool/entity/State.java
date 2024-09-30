package cn.wjc.tool.entity;

import lombok.Getter;

public class State {
    int FOLLOWER = 0;
    int CANDIDATE = 1;
    int LEADER = 2;

    @Getter
    enum Enum {
        FOLLOWER(0), CANDIDATE(1), LEADER(2);

        Enum(int code) {
            this.code = code;
        }

        final int code;

        public static Enum value(int i) {
            for (Enum value : Enum.values()) {
                if (value.code == i) {
                    return value;
                }
            }
            return null;
        }

    }
}
