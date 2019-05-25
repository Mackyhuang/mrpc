package vip.ifmm.protocol.response;

import vip.ifmm.enums.PacketCommandTypeEnum;
import vip.ifmm.protocol.Packet;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/22 </p>
 */
public class RpcResponsePacket extends Packet {

    private String responseId;

    private Object result;

    private boolean success;

    private Throwable error;

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    @Override
    public Byte getCommandType() {
        return PacketCommandTypeEnum.RPC_RESPONSE.getCode();
    }
}
