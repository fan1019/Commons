package com.fmh.commons.http;

import org.apache.http.HttpHost;
import org.omg.CORBA.PUBLIC_MEMBER;

public class HttpProxy extends Proxy {
	public enum Type {
		Anonymous, Transparent, Unkown
	}
	public Type type;

	public HttpProxy(String host, Integer port, Type type) {
		super(host, port, Protocol.Http);
		this.type = type;
	}

	@Override
	public String toString() {
		return host + ":" + port;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof Proxy) {
			return ((Proxy) object).host.equals(host) && ((Proxy) object).port.equals(port);
		}
		return false;
	}

	public HttpHost getHttpHost(){
        return new HttpHost(this.host,this.port);
    }
}
