package com.evolve.evolvebe.terms.interfaces;

import com.evolve.evolvebe.terms.application.TermsFacade;
import com.evolve.evolvebe.terms.domain.TermsCommand;
import com.evolve.evolvebe.terms.domain.TermsInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
public class TermsController {

    private static final Logger log = LoggerFactory.getLogger(TermsController.class);
    private final TermsFacade termsFacade;
    private final TermsDtoMapper termsDtoMapper;

    // 서비스 이용약관 조회
    @GetMapping("/getTos")
    public ResponseEntity<TermsResponse.TermsDetailResponse> getTos(
            @RequestParam String langCd
    ) {
        TermsRequest.GetTosRequest request = new TermsRequest.GetTosRequest();
        request.setLangCd(langCd);
        TermsCommand.GetTerms Command = termsDtoMapper.fromTos(request);
        TermsInfo.Main result = termsFacade.getTos(Command);
        TermsResponse.TermsDetailResponse response = termsDtoMapper.mapToResponse(result);

        return ResponseEntity.ok(response);
    }

    // 개인정보 처리방침 조회
    @GetMapping("/getTop")
    public ResponseEntity<TermsResponse.TermsDetailResponse> getTop(
            @RequestParam String langCd
    ) {
        TermsRequest.GetTopRequest request = new TermsRequest.GetTopRequest();
        request.setLangCd(langCd);
        TermsCommand.GetTerms command = termsDtoMapper.fromTop(request);
        TermsInfo.Main result = termsFacade.getTop(command);
        TermsResponse.TermsDetailResponse response = termsDtoMapper.mapTop(result);

        return ResponseEntity.ok(response);
    }
}
