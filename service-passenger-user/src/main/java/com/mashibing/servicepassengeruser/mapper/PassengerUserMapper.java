package com.mashibing.servicepassengeruser.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mashibing.servicepassengeruser.dto.PassengerUser;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Repository
public interface PassengerUserMapper extends BaseMapper<PassengerUser> {

}
