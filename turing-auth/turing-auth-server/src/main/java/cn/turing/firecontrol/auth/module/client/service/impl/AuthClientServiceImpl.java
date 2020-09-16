/*
 *  Copyright (C) 2018  Wanghaobin<463540703@qq.com>

 *  AG-Enterprise 企业版源码
 *  郑重声明:
 *  如果你从其他途径获取到，请告知老A传播人，奖励1000。
 *  老A将追究授予人和传播人的法律责任!

 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package cn.turing.firecontrol.auth.module.client.service.impl;

import cn.turing.firecontrol.auth.module.client.bean.ClientInfo;
import cn.turing.firecontrol.auth.module.client.entity.Client;
import cn.turing.firecontrol.auth.module.client.service.AuthClientService;
import cn.turing.firecontrol.auth.jwt.client.ClientTokenUtil;
import cn.turing.firecontrol.auth.module.client.mapper.ClientMapper;
import cn.turing.firecontrol.common.exception.auth.ClientInvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ace
 * @version 2017/9/10
 */
@Service
@CacheConfig(cacheNames = "clients")
public class AuthClientServiceImpl implements AuthClientService {
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private ClientTokenUtil clientTokenUtil;
    @Autowired
    private DiscoveryClient discovery;
    private ApplicationContext context;

    @Autowired
    public AuthClientServiceImpl(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public String apply(String clientId, String secret) throws Exception {
        Client client = getClient(clientId, secret);
        return clientTokenUtil.generateToken(new ClientInfo(client.getCode(),client.getName(),client.getId().toString()));
    }

    private Client getClient(String clientId, String secret) {
        Client client = new Client();
        client.setCode(clientId);
        client = clientMapper.selectOne(client);
        if(client==null||!client.getSecret().equals(secret)){
            throw new ClientInvalidException("Client not found or Client secret is error!");
        }
        return client;
    }

    @Cacheable(key = "'method:client:validate:'+#clientId")
    @Override
    public void validate(String clientId, String secret) throws Exception {
        Client client = new Client();
        client.setCode(clientId);
        client = clientMapper.selectOne(client);
        if(client==null||!client.getSecret().equals(secret)){
            throw new ClientInvalidException("Client not found or Client secret is error!");
        }
    }

    @Cacheable(key = "'method:client:allowed:'+#clientId")
    @Override
    public List<String> getAllowedClient(String clientId, String secret) {
        Client info = this.getClient(clientId, secret);
        List<String> clients = clientMapper.selectAllowedClient(info.getId() + "");
        if(clients==null) {
            new ArrayList<String>();
        }
        return clients;
    }

    @Cacheable(key = "'method:service:allowed:'+#clientId")
    @Override
    public List<String> getAllowedClient(String serviceId) {
        Client info = getClient(serviceId);
        List<String> clients = clientMapper.selectAllowedClient(info.getId() + "");
        if(clients==null) {
            new ArrayList<String>();
        }
        return clients;
    }

    private Client getClient(String clientId) {
        Client client = new Client();
        client.setCode(clientId);
        client = clientMapper.selectOne(client);
        return client;
    }
}
