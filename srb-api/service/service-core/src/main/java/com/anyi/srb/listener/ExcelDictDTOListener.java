package com.anyi.srb.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.anyi.srb.entity.dto.ExcelDictDTO;
import com.anyi.srb.entity.Dict;
import com.anyi.srb.service.DictService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * @author 安逸i
 * @version 1.0
 */

@Slf4j
//@AllArgsConstructor //全参
@NoArgsConstructor //无参
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDTO> {

    private DictService dictService;


    public ExcelDictDTOListener(DictService dictService){
        this.dictService =dictService;
    }
    /**
     * 每次读出一条数据就会执行这个方法
     * @param excelDictDTO
     * @param analysisContext
     */
    @Override
    public void invoke(ExcelDictDTO excelDictDTO, AnalysisContext analysisContext) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(excelDictDTO,dict);
        dictService.save(dict);

    }

    /**
     * 所有读完后执行
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("import finished ...");
    }
}
