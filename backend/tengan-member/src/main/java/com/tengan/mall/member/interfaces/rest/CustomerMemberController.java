package com.tengan.mall.member.interfaces.rest;

import com.tengan.mall.member.application.address.AddressSummary;
import com.tengan.mall.member.application.address.CreateAddressCommand;
import com.tengan.mall.member.application.address.CreateAddressUseCase;
import com.tengan.mall.member.application.address.DeleteAddressCommand;
import com.tengan.mall.member.application.address.DeleteAddressUseCase;
import com.tengan.mall.member.application.address.ListAddressesUseCase;
import com.tengan.mall.member.application.address.SetDefaultAddressCommand;
import com.tengan.mall.member.application.address.SetDefaultAddressUseCase;
import com.tengan.mall.member.application.address.UpdateAddressCommand;
import com.tengan.mall.member.application.address.UpdateAddressUseCase;
import com.tengan.mall.member.application.member.GetProfileUseCase;
import com.tengan.mall.member.application.member.UpdateProfileCommand;
import com.tengan.mall.member.application.member.UpdateProfileUseCase;
import com.tengan.mall.member.interfaces.rest.dto.AddressResponse;
import com.tengan.mall.member.interfaces.rest.dto.CreateAddressRequest;
import com.tengan.mall.member.interfaces.rest.dto.CreateAddressResponse;
import com.tengan.mall.member.interfaces.rest.dto.ProfileResponse;
import com.tengan.mall.member.interfaces.rest.dto.UpdateAddressRequest;
import com.tengan.mall.member.interfaces.rest.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * zero-trust：這裡直接用 userJwtDecoder 驗簽解出 sub（=tengan-auth 的 accountId=memberId），
 * 不依賴 Gateway 轉發任何明文身份 header（見 jwt_zero_trust_decision 記憶）。
 */
@RestController
@RequestMapping("/api/customer/member")
public class CustomerMemberController {

    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final ListAddressesUseCase listAddressesUseCase;
    private final CreateAddressUseCase createAddressUseCase;
    private final UpdateAddressUseCase updateAddressUseCase;
    private final DeleteAddressUseCase deleteAddressUseCase;
    private final SetDefaultAddressUseCase setDefaultAddressUseCase;

    public CustomerMemberController(GetProfileUseCase getProfileUseCase, UpdateProfileUseCase updateProfileUseCase,
            ListAddressesUseCase listAddressesUseCase, CreateAddressUseCase createAddressUseCase,
            UpdateAddressUseCase updateAddressUseCase, DeleteAddressUseCase deleteAddressUseCase,
            SetDefaultAddressUseCase setDefaultAddressUseCase) {
        this.getProfileUseCase = getProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.listAddressesUseCase = listAddressesUseCase;
        this.createAddressUseCase = createAddressUseCase;
        this.updateAddressUseCase = updateAddressUseCase;
        this.deleteAddressUseCase = deleteAddressUseCase;
        this.setDefaultAddressUseCase = setDefaultAddressUseCase;
    }

    @GetMapping("/profile")
    public ProfileResponse profile(@AuthenticationPrincipal Jwt jwt) {
        var result = getProfileUseCase.get(memberId(jwt));
        return new ProfileResponse(result.id(), result.username(), result.phone(), result.nickname(),
                result.avatarUrl());
    }

    @PutMapping("/profile")
    public void updateProfile(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateProfileRequest request) {
        updateProfileUseCase.update(new UpdateProfileCommand(memberId(jwt), request.nickname(),
                request.avatarUrl()));
    }

    @GetMapping("/addresses")
    public List<AddressResponse> addresses(@AuthenticationPrincipal Jwt jwt) {
        return listAddressesUseCase.list(memberId(jwt)).stream().map(this::toResponse).toList();
    }

    @PostMapping("/addresses")
    public ResponseEntity<CreateAddressResponse> createAddress(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateAddressRequest request) {
        var result = createAddressUseCase.create(new CreateAddressCommand(memberId(jwt), request.receiverName(),
                request.receiverPhone(), request.city(), request.district(), request.postalCode(), request.street(),
                request.isDefault()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateAddressResponse(result.id()));
    }

    @PutMapping("/addresses/{id}")
    public void updateAddress(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
            @Valid @RequestBody UpdateAddressRequest request) {
        updateAddressUseCase.update(new UpdateAddressCommand(memberId(jwt), id, request.receiverName(),
                request.receiverPhone(), request.city(), request.district(), request.postalCode(),
                request.street()));
    }

    @DeleteMapping("/addresses/{id}")
    public void deleteAddress(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        deleteAddressUseCase.delete(new DeleteAddressCommand(memberId(jwt), id));
    }

    @PutMapping("/addresses/{id}/default")
    public void setDefaultAddress(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        setDefaultAddressUseCase.setDefault(new SetDefaultAddressCommand(memberId(jwt), id));
    }

    private Long memberId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    private AddressResponse toResponse(AddressSummary summary) {
        return new AddressResponse(summary.id(), summary.receiverName(), summary.receiverPhone(), summary.city(),
                summary.district(), summary.postalCode(), summary.street(), summary.isDefault());
    }
}
