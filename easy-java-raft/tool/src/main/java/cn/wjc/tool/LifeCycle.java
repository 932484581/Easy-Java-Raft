package cn.wjc.tool;

public interface LifeCycle {
    /**
     * @description: 初始化.
     * @return {*}
     * @author: WJC
     */
    void init() throws Throwable;

    /**
     * @description: 关闭资源.
     * @return {*}
     * @author: WJC
     */
    void destroy() throws Throwable;
}
