package com.fmh.commons.http;

public class Proxy {
	public enum Protocol {
		Direct, Socket, Http
	}
	public String host;
	public Integer port;
	public Protocol protocol;

	public Proxy(String host, Integer port, Protocol protocol) {
		this.host = host;
		this.port = port;
		this.protocol = protocol;
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
}
