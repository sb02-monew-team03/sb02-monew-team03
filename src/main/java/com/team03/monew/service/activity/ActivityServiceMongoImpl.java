package com.team03.monew.service.activity;

import com.team03.monew.dto.user.UserActivityDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityServiceMongoImpl implements ActivityService {

    @Override
    public UserActivityDto getUserActivity(UUID userId, UUID requesterId) {
        return null;
    }
}
