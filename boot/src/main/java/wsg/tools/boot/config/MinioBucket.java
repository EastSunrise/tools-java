package wsg.tools.boot.config;

import io.minio.policy.PolicyType;
import java.util.HashMap;
import java.util.Map;

/**
 * A bucket in MinIO server.
 *
 * @author Kingen
 * @since 2021/3/23
 */
class MinioBucket {

    private static final String POLICY_PREFIX_ALL = "*";

    private final String name;
    private final Map<String, PolicyType> policies = new HashMap<>(1);

    MinioBucket(String name) {
        this.name = name;
    }

    MinioBucket setPolicyAll(PolicyType policy) {
        policies.put(POLICY_PREFIX_ALL, policy);
        return this;
    }

    public String getName() {
        return name;
    }

    Map<String, PolicyType> getPolicies() {
        return policies;
    }
}
