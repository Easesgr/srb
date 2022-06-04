package com.anyi.srb.service.impl;

import com.alibaba.excel.EasyExcel;
import com.anyi.srb.entity.dto.ExcelDictDTO;
import com.anyi.srb.entity.Dict;
import com.anyi.srb.listener.ExcelDictDTOListener;
import com.anyi.srb.mapper.DictMapper;
import com.anyi.srb.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author anyi
 * @since 2022-05-31
 */
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    /**
     * excel信息导入
     * @param inputStream
     */
    @Override
    public void importData(InputStream inputStream) {
        EasyExcel.read(inputStream, ExcelDictDTO.class, new ExcelDictDTOListener(this)).sheet().doRead();
    }

    @Override
    public List<ExcelDictDTO> listDictData() {
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        List<Dict> list = list(wrapper);
        List<ExcelDictDTO> excelDictDTOS = list.stream().map(item->{
            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(item, excelDictDTO);
            return excelDictDTO;
        }).collect(Collectors.toList());
        return excelDictDTOS;
    }

    /**
     * 根据父节点id查询列表
     * @param parentId
     * @return
     */
    @Override
    public List<Dict> listByParentId(Long parentId) {
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getParentId,parentId);
        List<Dict> list = list(wrapper);
        list = list.stream().map(item->{
            List<Dict> childrenList = list(new LambdaQueryWrapper<Dict>().eq(Dict::getParentId, item.getId()));
            if (childrenList.size() > 0){
                item.setHasChildren(true);
            }
            return item;
        }).collect(Collectors.toList());

        return list;
    }

    /**
     * 根据dictcode查询分类信息
     * @param dictCode
     * @return
     */
    @Override
    public List<Dict> getByDictCode(String dictCode) {
        // 查询出当前父节点
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getDictCode,dictCode);
        Dict one = getOne(wrapper);

        // 根据父节点查询出所有的子节点
        List<Dict> list = listByParentId(one.getId());
        return list;
    }

    /**
     * 根据 value 和  dictcode获取信息
     * @param dictCode
     * @param value
     * @return
     */
    @Override
    public String getNameByParentDictCodeAndValue(String dictCode, Integer value) {

        // 获取dictcode 从而获取到id
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getDictCode,dictCode);
        Long id = getOne(wrapper).getId();

        // 根据id 获取所有字
        String name = getOne(new LambdaQueryWrapper<Dict>()
                .eq(Dict::getParentId, id)
                .eq(Dict::getValue, value)).getName();
        return name;
    }
}
