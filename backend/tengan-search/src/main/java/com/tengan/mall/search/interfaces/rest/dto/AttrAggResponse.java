package com.tengan.mall.search.interfaces.rest.dto;

import java.util.List;

public record AttrAggResponse(String attrKey, String attrName, List<AttrValueCountResponse> values) {
}
