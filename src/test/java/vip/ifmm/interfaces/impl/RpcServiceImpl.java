package vip.ifmm.interfaces.impl;

import vip.ifmm.annotation.Expose;
import vip.ifmm.interfaces.Rpcinterface;
import vip.ifmm.server.RpcServer;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/25 </p>
 */
@Expose(Rpcinterface.class)
public class RpcServiceImpl implements Rpcinterface {
    @Override
    public int add(int a1, int a2) {
        return a1 + a2;
    }
}
