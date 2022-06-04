<template>
  <div class="app-container">
    <!-- 表格 -->
    <el-table :data="list" border stripe>
      <el-table-column type="index" width="50" />
      <el-table-column prop="borrowAmount" label="借款额度" />
      <el-table-column prop="integralStart" label="积分区间开始" />
      <el-table-column prop="integralEnd" label="积分区间结束" />
      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
          <el-button
            type="danger"
            size="mini"
            icon="el-icon-delete"
            @click="removeById(scope.row.id)"
            class="delete"
          >
            删除
          </el-button>
          <router-link
            :to="'/core/integral-grade/edit/' + scope.row.id"
            style="margin-right: 5px"
          >
            <el-button
              type="primary"
              size="mini"
              icon="el-icon-edit"
              
            >
              修改
            </el-button>
          </router-link>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import intergal from "@/api/integal";
export default {
  data() {
    return {
      list: [],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      intergal.getIntergalList().then((res) => {
        this.list = res.data.items;
      });
    },
    removeById(id){
    this.$confirm('此操作将永久删除该积分, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          intergal.deleteIntergalById(id).then(res=>{
            this.$message({
            type: 'success',
            message: '删除成功!'
          });
          this.getList();
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });          
        });

    }
  },
};
</script>
<style scoped>
.delete{
  margin-right: 10px;
}
</style>></style>
