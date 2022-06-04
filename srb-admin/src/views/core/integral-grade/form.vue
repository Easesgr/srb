<template>
  <div class="app-container">
    <!-- 输入表单 -->
    <el-form label-width="120px">
      <el-form-item label="借款额度">
        <el-input-number v-model="integralGrade.borrowAmount" :min="0" />
      </el-form-item>
      <el-form-item label="积分区间开始">
        <el-input-number v-model="integralGrade.integralStart" :min="0" />
      </el-form-item>
      <el-form-item label="积分区间结束">
        <el-input-number v-model="integralGrade.integralEnd" :min="0" />
      </el-form-item>
      <el-form-item>
        <el-button
          :disabled="saveBtnDisabled"
          type="primary"
          @click="saveOrUpdate()"
        >
          保存
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import intergal from "@/api/integal";

export default {
  data() {
    return {
      saveBtnDisabled: false,
      integralGrade: {
        borrowAmount: 0,
        integralStart: 0,
        integralEnd: 0,
      },
    };
  },
  created() {
    if (this.$route.params.id) {
      this.getInfo(this.$route.params.id);
    }
  },
  methods: {
    saveOrUpdate() {
      if (this.$route.params.id) {
        this.update();
      } else {
        this.save();
      }
    },
    save() {
      intergal.saveIntergal(this.integralGrade).then((res) => {
        this.$message({
          type: "success",
          message: "保存成功！",
        });
        this.$router.push("/core/integral-grade/list");
      });
    },
    update() {
      intergal.updateIntergal(this.integralGrade).then((res) => {
        this.$message({
          type: "success",
          message: "修改成功！",
        });
        this.$router.push("/core/integral-grade/list");
      });
    },
    getInfo(id) {
      intergal.getIntergalById(id).then((res) => {
        this.integralGrade = res.data.item;
      });
    },
  },
};
</script>
<style></style>
