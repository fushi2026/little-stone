<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import {
  Plus,
  Trash2,
  GripVertical,
  Save,
  Eye,
  Copy,
  Settings
} from '@element-plus/icons-vue'
import {
  ElButton,
  ElInput,
  ElSelect,
  ElOption,
  ElSwitch,
  ElModal,
  ElForm,
  ElFormItem,
  ElInputNumber,
  ElTextarea,
  ElDrawer,
  ElCard,
  ElAlert
} from 'element-plus'
import type { FormFieldDTO } from '../../api/form'
import DynamicForm from './DynamicForm.vue'

interface Props {
  modelValue?: FormFieldDTO[]
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => []
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: FormFieldDTO[]): void
  (e: 'save', value: FormFieldDTO[]): void
}>()

const fields = ref<FormFieldDTO[]>([...(props.modelValue || [])])

const emitChange = () => {
  emit('update:modelValue', [...fields.value])
}

const fieldTypes = [
  { value: 'text', label: '单行文本' },
  { value: 'textarea', label: '多行文本' },
  { value: 'number', label: '数字' },
  { value: 'select', label: '下拉选择' },
  { value: 'radio', label: '单选框' },
  { value: 'checkbox', label: '多选框' },
  { value: 'date', label: '日期' },
  { value: 'time', label: '时间' },
  { value: 'switch', label: '开关' },
  { value: 'password', label: '密码' }
]

const defaultField = (type: string): FormFieldDTO => ({
  fieldType: type,
  fieldName: `field_${Date.now()}`,
  label: '',
  placeholder: '',
  required: false,
  options: type === 'select' || type === 'radio' || type === 'checkbox' ? [] : undefined
})

const addField = (type: string) => {
  fields.value.push(defaultField(type))
  emitChange()
}

const removeField = (index: number) => {
  fields.value.splice(index, 1)
  emitChange()
}

const duplicateField = (index: number) => {
  const field = { ...fields.value[index] }
  field.fieldName = `field_${Date.now()}`
  fields.value.splice(index + 1, 0, field)
  emitChange()
}

const updateField = (index: number, updates: Partial<FormFieldDTO>) => {
  fields.value[index] = { ...fields.value[index], ...updates }
  emitChange()
}

const moveField = (fromIndex: number, toIndex: number) => {
  const [removed] = fields.value.splice(fromIndex, 1)
  fields.value.splice(toIndex, 0, removed)
  emitChange()
}

const selectedFieldIndex = ref<number | null>(null)

const selectedField = computed(() => {
  if (selectedFieldIndex.value === null) return null
  return fields.value[selectedFieldIndex.value]
})

const showPreview = ref(false)

const previewData = ref<Record<string, any>>({})

const handleSave = () => {
  emit('save', [...fields.value])
}

const addOption = () => {
  if (selectedField.value && selectedField.value.options) {
    selectedField.value.options.push({ value: '', label: '' })
    emitChange()
  }
}

const removeOption = (optionIndex: number) => {
  if (selectedField.value && selectedField.value.options) {
    selectedField.value.options.splice(optionIndex, 1)
    emitChange()
  }
}
</script>

<template>
  <div class="form-builder">
    <div class="builder-header">
      <h2>表单设计器</h2>
      <div class="header-actions">
        <ElButton icon="Search" @click="showPreview = true">
          预览
        </ElButton>
        <ElButton type="primary" icon="Upload" @click="handleSave">
          保存表单
        </ElButton>
      </div>
    </div>

    <div class="builder-content">
      <div class="toolbar">
        <h3>组件库</h3>
        <div class="tool-items">
          <div
            v-for="type in fieldTypes"
            :key="type.value"
            class="tool-item"
            @click="addField(type.value)"
          >
            <Plus class="tool-icon" />
            <span>{{ type.label }}</span>
          </div>
        </div>
      </div>

      <div class="canvas">
        <div v-if="fields.length === 0" class="empty-canvas">
          <ElAlert
            title="从左侧组件库拖拽或点击添加表单字段"
            type="info"
            :closable="false"
          />
        </div>

        <div v-else class="field-list">
          <div
            v-for="(field, index) in fields"
            :key="field.fieldName"
            class="field-item"
            :class="{ selected: selectedFieldIndex === index }"
            @click="selectedFieldIndex = index"
          >
            <div class="field-handle">
              <GripVertical class="handle-icon" />
            </div>
            <div class="field-content">
              <div class="field-header">
                <span class="field-label">{{ field.label || '未命名字段' }}</span>
                <span class="field-type">{{ fieldTypes.find(t => t.value === field.fieldType)?.label }}</span>
                <span v-if="field.required" class="field-required">必填</span>
              </div>
              <div class="field-preview">
                <ElInput
                  v-if="['text', 'password'].includes(field.fieldType)"
                  :placeholder="field.placeholder"
                  :disabled="true"
                  size="small"
                  class="preview-input"
                />
                <ElTextarea
                  v-else-if="field.fieldType === 'textarea'"
                  :placeholder="field.placeholder"
                  :disabled="true"
                  size="small"
                  class="preview-textarea"
                />
                <ElSelect
                  v-else-if="field.fieldType === 'select'"
                  :placeholder="field.placeholder"
                  :disabled="true"
                  size="small"
                  class="preview-select"
                />
                <div v-else-if="field.fieldType === 'radio'" class="preview-radio">
                  <span v-for="(opt, i) in (field.options || []).slice(0, 3)" :key="i">{{ opt.label }} </span>
                </div>
                <div v-else-if="field.fieldType === 'checkbox'" class="preview-checkbox">
                  <span v-for="(opt, i) in (field.options || []).slice(0, 3)" :key="i">{{ opt.label }} </span>
                </div>
                <ElInputNumber
                  v-else-if="field.fieldType === 'number'"
                  :disabled="true"
                  size="small"
                  class="preview-number"
                />
                <ElSwitch
                  v-else-if="field.fieldType === 'switch'"
                  :disabled="true"
                  class="preview-switch"
                />
              </div>
            </div>
            <div class="field-actions">
              <ElButton
                icon="Copy"
                size="small"
                circle
                @click.stop="duplicateField(index)"
              />
              <ElButton
                icon="Settings"
                size="small"
                circle
                @click.stop="selectedFieldIndex = index"
              />
              <ElButton
                icon="Delete"
                size="small"
                circle
                type="danger"
                @click.stop="removeField(index)"
              />
            </div>
          </div>
        </div>
      </div>

      <ElDrawer
        v-model="selectedFieldIndex !== null"
        title="字段设置"
        direction="rtl"
        size="400px"
      >
        <template v-if="selectedField">
          <ElForm :model="selectedField" label-width="80px">
            <ElFormItem label="字段类型">
              <ElSelect
                v-model="selectedField.fieldType"
                @change="emitChange"
              >
                <ElOption
                  v-for="type in fieldTypes"
                  :key="type.value"
                  :value="type.value"
                  :label="type.label"
                />
              </ElSelect>
            </ElFormItem>
            <ElFormItem label="字段名称">
              <ElInput
                v-model="selectedField.fieldName"
                @input="emitChange"
                placeholder="用于表单数据的key"
              />
            </ElFormItem>
            <ElFormItem label="显示标签">
              <ElInput
                v-model="selectedField.label"
                @input="emitChange"
                placeholder="用户看到的字段名称"
              />
            </ElFormItem>
            <ElFormItem label="占位提示">
              <ElInput
                v-model="selectedField.placeholder"
                @input="emitChange"
                placeholder="输入框内的提示文字"
              />
            </ElFormItem>
            <ElFormItem label="必填字段">
              <ElSwitch
                v-model="selectedField.required"
                @change="emitChange"
              />
            </ElFormItem>
            <ElFormItem v-if="selectedField.fieldType === 'number'" label="最小值">
              <ElInputNumber
                v-model="selectedField.min"
                @input="emitChange"
              />
            </ElFormItem>
            <ElFormItem v-if="selectedField.fieldType === 'number'" label="最大值">
              <ElInputNumber
                v-model="selectedField.max"
                @input="emitChange"
              />
            </ElFormItem>
            <ElFormItem v-if="['text', 'textarea'].includes(selectedField.fieldType)" label="最小长度">
              <ElInputNumber
                v-model="selectedField.minLength"
                @input="emitChange"
              />
            </ElFormItem>
            <ElFormItem v-if="['text', 'textarea'].includes(selectedField.fieldType)" label="最大长度">
              <ElInputNumber
                v-model="selectedField.maxLength"
                @input="emitChange"
              />
            </ElFormItem>
            <ElFormItem v-if="['text', 'textarea'].includes(selectedField.fieldType)" label="验证规则">
              <ElInput
                v-model="selectedField.pattern"
                @input="emitChange"
                placeholder="正则表达式"
              />
            </ElFormItem>
            <ElFormItem v-if="selectedField.pattern" label="错误提示">
              <ElInput
                v-model="selectedField.validationMessage"
                @input="emitChange"
                placeholder="验证失败时的提示文字"
              />
            </ElFormItem>
            <ElFormItem v-if="['select', 'radio', 'checkbox'].includes(selectedField.fieldType)" label="选项列表">
              <div v-if="selectedField.options" class="options-list">
                <div
                  v-for="(option, optIndex) in selectedField.options"
                  :key="optIndex"
                  class="option-item"
                >
                  <ElInput
                    v-model="option.value"
                    @input="emitChange"
                    placeholder="值"
                    size="small"
                    class="option-value"
                  />
                  <ElInput
                    v-model="option.label"
                    @input="emitChange"
                    placeholder="显示文本"
                    size="small"
                    class="option-label"
                  />
                  <ElButton
                    icon="Delete"
                    size="small"
                    type="danger"
                    @click="removeOption(optIndex)"
                  />
                </div>
              </div>
              <ElButton type="primary" size="small" @click="addOption">
                + 添加选项
              </ElButton>
            </ElFormItem>
          </ElForm>
        </template>
      </ElDrawer>
    </div>

    <ElModal
      v-model="showPreview"
      title="表单预览"
      width="600px"
      :footer="false"
    >
      <DynamicForm :fields="fields" v-model="previewData" />
    </ElModal>
  </div>
</template>

<style lang="scss" scoped>
.form-builder {
  min-height: 100vh;
  background: #f5f7fa;

  .builder-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 24px;
    background: #fff;
    border-bottom: 1px solid #ebeef5;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    h2 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
    }

    .header-actions {
      display: flex;
      gap: 12px;
    }
  }

  .builder-content {
    display: flex;
    height: calc(100vh - 72px);
  }

  .toolbar {
    width: 240px;
    background: #fff;
    padding: 16px;
    border-right: 1px solid #ebeef5;
    overflow-y: auto;

    h3 {
      margin: 0 0 12px 0;
      font-size: 14px;
      font-weight: 600;
      color: #606266;
    }

    .tool-items {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .tool-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 12px;
      background: #f5f7fa;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        background: #e8f4ff;
        transform: translateX(4px);
      }

      .tool-icon {
        color: #409eff;
      }

      span {
        font-size: 13px;
        color: #606266;
      }
    }
  }

  .canvas {
    flex: 1;
    padding: 24px;
    overflow-y: auto;

    .empty-canvas {
      margin: 48px auto;
      max-width: 480px;
    }

    .field-list {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    .field-item {
      display: flex;
      align-items: stretch;
      background: #fff;
      border: 2px solid transparent;
      border-radius: 12px;
      padding: 12px;
      transition: all 0.2s;
      cursor: pointer;

      &:hover {
        border-color: #e8f4ff;
      }

      &.selected {
        border-color: #409eff;
        background: #f0f8ff;
      }

      .field-handle {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 0 8px;
        color: #c0c4cc;
        cursor: move;

        .handle-icon {
          font-size: 18px;
        }
      }

      .field-content {
        flex: 1;
        min-width: 0;

        .field-header {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;

          .field-label {
            font-size: 14px;
            font-weight: 500;
            color: #303133;
          }

          .field-type {
            font-size: 12px;
            color: #909399;
            padding: 2px 6px;
            background: #f5f7fa;
            border-radius: 4px;
          }

          .field-required {
            font-size: 12px;
            color: #f56c6c;
            padding: 2px 6px;
            background: #fef0f0;
            border-radius: 4px;
          }
        }

        .field-preview {
          .preview-input,
          .preview-textarea,
          .preview-select,
          .preview-number {
            width: 100%;
            background: #fafafa;
          }

          .preview-textarea {
            min-height: 60px;
          }

          .preview-radio,
          .preview-checkbox {
            font-size: 13px;
            color: #606266;
          }
        }
      }

      .field-actions {
        display: flex;
        flex-direction: column;
        justify-content: center;
        gap: 8px;
        margin-left: 12px;
        padding-left: 12px;
        border-left: 1px solid #ebeef5;
      }
    }
  }

  .options-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-bottom: 8px;

    .option-item {
      display: flex;
      align-items: center;
      gap: 8px;

      .option-value,
      .option-label {
        flex: 1;
      }
    }
  }
}
</style>
