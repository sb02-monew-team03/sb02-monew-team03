package com.team03.monew.service.activity;

import com.team03.monew.dto.user.UserActivityDto;
import java.util.UUID;

public interface ActivityService {

    UserActivityDto getUserActivity(UUID userId);
}
