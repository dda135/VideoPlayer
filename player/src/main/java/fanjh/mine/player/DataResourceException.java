package fanjh.mine.player;

/**
* @author fanjh
* @date 2018/1/11 10:01
* @description 资源异常
* @note 一般就是当前资源无法读取或者未找到等错误
**/
public class DataResourceException extends Exception{

    public DataResourceException(Throwable cause) {
        super(cause);
    }
}
