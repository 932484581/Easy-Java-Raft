package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * @description: 客户端的指令
 * @return {*}
 * @author: WJC
 */
@Data
@Builder
public class Command implements Serializable {
    public String command;

    public Command(String command) {
        this.command = command;
    }
}
