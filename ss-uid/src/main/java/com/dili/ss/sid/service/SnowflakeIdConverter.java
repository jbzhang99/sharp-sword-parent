package com.dili.ss.sid.service;


import com.dili.ss.sid.dto.SnowflakeId;

public interface SnowflakeIdConverter {
  long convert(SnowflakeId id);

  SnowflakeId convert(long id);
}
