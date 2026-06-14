package cn.iocoder.yudao.module.lab.service.domainpack;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lab.controller.admin.domainpack.vo.LabDomainPackSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.domain.LabDomainProfileDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.domainpack.LabDomainPackDO;
import cn.iocoder.yudao.module.lab.dal.mysql.domainpack.LabDomainPackMapper;
import cn.iocoder.yudao.module.lab.service.domain.LabDomainProfileService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.DOMAIN_PACK_PUBLISHED_IMMUTABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LabDomainPackServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LabDomainPackServiceImpl service;

    @Mock
    private LabDomainPackMapper domainPackMapper;
    @Mock
    private LabDomainProfileService domainProfileService;

    @Test
    void updateDomainPack_shouldRejectPublishedPack() {
        LabDomainPackDO existing = domainPack(1L, "FOOD_ROUTINE", "1.0", "published");
        when(domainPackMapper.selectById(1L)).thenReturn(existing);

        LabDomainPackSaveReqVO reqVO = saveReq(1L, "FOOD_ROUTINE", "1.0", "published");

        ServiceException ex = assertThrows(ServiceException.class, () -> service.updateDomainPack(reqVO));

        assertEquals(DOMAIN_PACK_PUBLISHED_IMMUTABLE.getCode(), ex.getCode());
    }

    @Test
    void publishDomainPack_shouldMoveDraftToPublished() {
        LabDomainPackDO existing = domainPack(1L, "FOOD_ROUTINE", "1.0", "draft");
        when(domainPackMapper.selectById(1L)).thenReturn(existing);

        service.publishDomainPack(1L);

        verify(domainPackMapper).updateById(argThat((LabDomainPackDO pack) -> "published".equals(pack.getStatus())));
    }

    @Test
    void copyDomainPackVersion_shouldCreateDraftVersion() {
        LabDomainPackDO existing = domainPack(1L, "FOOD_ROUTINE", "1.0", "published");
        when(domainPackMapper.selectById(1L)).thenReturn(existing);
        when(domainPackMapper.selectByPackCodeAndVersion("FOOD_ROUTINE", "1.1")).thenReturn(null);
        when(domainPackMapper.insert(any(LabDomainPackDO.class))).thenAnswer(invocation -> {
            LabDomainPackDO inserted = invocation.getArgument(0);
            inserted.setId(2L);
            return 1;
        });

        Long copiedId = service.copyDomainPackVersion(1L, "1.1");

        assertNotNull(copiedId);
        verify(domainPackMapper).insert(argThat((LabDomainPackDO pack) ->
                "FOOD_ROUTINE".equals(pack.getPackCode())
                        && "1.1".equals(pack.getPackVersion())
                        && "draft".equals(pack.getStatus())));
    }

    @Test
    void updateDomainPack_shouldAllowDraftPack() {
        LabDomainPackDO existing = domainPack(1L, "FOOD_ROUTINE", "1.0", "draft");
        when(domainPackMapper.selectById(1L)).thenReturn(existing);
        when(domainProfileService.getDomainProfile(10L)).thenReturn(new LabDomainProfileDO());
        when(domainPackMapper.selectByPackCodeAndVersion("FOOD_ROUTINE", "1.0")).thenReturn(existing);

        service.updateDomainPack(saveReq(1L, "FOOD_ROUTINE", "1.0", "draft"));

        verify(domainPackMapper).updateById(argThat((LabDomainPackDO pack) -> Long.valueOf(1L).equals(pack.getId())
                && "FOOD_ROUTINE".equals(pack.getPackCode())
                && "1.0".equals(pack.getPackVersion())
                && "draft".equals(pack.getStatus())));
    }

    private static LabDomainPackSaveReqVO saveReq(Long id, String packCode, String packVersion, String status) {
        LabDomainPackSaveReqVO reqVO = new LabDomainPackSaveReqVO();
        reqVO.setId(id);
        reqVO.setDomainId(10L);
        reqVO.setPackCode(packCode);
        reqVO.setPackName("食品常规检测方案包");
        reqVO.setPackVersion(packVersion);
        reqVO.setIndustry("食品");
        reqVO.setApplicationScope("食品理化与微生物常规项目");
        reqVO.setWorkflowSchema("{\"stages\":[\"request\",\"sample\",\"task\",\"report\"]}");
        reqVO.setTemplateSchema("{\"templates\":[\"report\"]}");
        reqVO.setStatus(status);
        return reqVO;
    }

    private static LabDomainPackDO domainPack(Long id, String packCode, String packVersion, String status) {
        LabDomainPackDO domainPack = new LabDomainPackDO();
        domainPack.setId(id);
        domainPack.setDomainId(10L);
        domainPack.setPackCode(packCode);
        domainPack.setPackName("食品常规检测方案包");
        domainPack.setPackVersion(packVersion);
        domainPack.setIndustry("食品");
        domainPack.setApplicationScope("食品理化与微生物常规项目");
        domainPack.setWorkflowSchema("{\"stages\":[\"request\",\"sample\",\"task\",\"report\"]}");
        domainPack.setTemplateSchema("{\"templates\":[\"report\"]}");
        domainPack.setStatus(status);
        return domainPack;
    }

}
