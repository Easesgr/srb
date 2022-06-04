package com.anyi.srb.service;

import com.anyi.srb.entity.dto.ExcelDictDTO;
import com.anyi.srb.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author anyi
 * @since 2022-05-31
 */
public interface DictService extends IService<Dict> {

    void importData(InputStream inputStream);

    public List<ExcelDictDTO> listDictData();

    List<Dict> listByParentId(Long parentId);

    List<Dict> getByDictCode(String dictCode);

    String getNameByParentDictCodeAndValue(String education, Integer education1);
}
