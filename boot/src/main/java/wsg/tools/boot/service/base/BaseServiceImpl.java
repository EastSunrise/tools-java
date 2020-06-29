package wsg.tools.boot.service.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.function.Function;

/**
 * Base class for Service, implementing common functions.
 *
 * @author Kingen
 * @since 2020/6/24
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    protected <F> QueryWrapper<T> invokeWrapper(F f, Function<F, QueryWrapper<T>> function) {
        if (f == null) {
            return null;
        }
        return function.apply(f);
    }
}
