<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import {
  ElInput,
  ElInputNumber,
  ElSelect,
  ElOption,
  ElRadioGroup,
  ElRadio,
  ElCheckboxGroup,
  ElCheckbox,
  ElDatePicker,
  ElTimePicker,
  ElSwitch,
  ElTextarea
} from 'element-plus'
import type { FormFieldDTO } from '../../api/form'

interface Props {
  fields: FormFieldDTO[]
  modelValue?: Record<string, any>
  disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => ({}),
  disabled: false
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: Record<string, any>): void
  (e: 'validate', valid: boolean): void
}>()

const formData = reactive<Record<string, any>>({})

watch(
  () => props.modelValue,
  (newVal) => {
    if (newVal) {
      Object.assign(formData, newVal)
    }
  },
  { immediate: true, deep: true }
)

watch(
  formData,
  (newVal) => {
    emit('update:modelValue', { ...newVal })
  },
  { deep: true }
)

const handleInput = (fieldName: string, value: any) => {
  formData[fieldName] = value
}

const getFieldValue = (fieldName: string): any => {
  return formData[fieldName]
}

const validateField = (field: FormFieldDTO): boolean => {
  const value = getFieldValue(field.fieldName)
  
  if (field.required && !value && value !== 0 && value !== false) {
    return false
  }
  
  if (field.pattern && value) {
    const reg = new RegExp(field.pattern)
    if (!reg.test(value)) {
      return false
    }
  }
  
  if (field.minLength !== undefined && typeof value === 'string') {
    if (value.length < field.minLength) {
      return false
    }
  }
  
  if (field.maxLength !== undefined && typeof value === 'string') {
    if (value.length > field.maxLength) {
      return false
    }
  }
  
  if (field.min !== undefined && typeof value === 'number') {
    if (value < field.min) {
      return false
    }
  }
  
  if (field.max !== undefined && typeof value === 'number') {
    if (value > field.max) {
      return false
    }
  }
  
  return true
}

const validate = (): boolean => {
  let valid = true
  for (const field of props.fields) {
    if (!validateField(field)) {
      valid = false
      break
    }
  }
  emit('validate', valid)
  return valid
}

defineExpose({
  validate,
  formData
})
</script>

<template>
  <div class="dynamic-form">
    <template v-for="(field, index) in fields" :key="field.fieldName || index">
      <div class="form-item" :class="{ 'is-required': field.required }">
        <label class="form-label">
          {{ field.label }}
          <span v-if="field.required" class="required-mark">*</span>
        </label>
        
        <ElInput
          v-else-if="field.fieldType === 'text'"
          :model-value="getFieldValue(field.fieldName)"
          :placeholder="field.placeholder"
          :disabled="disabled"
          :minlength="field.minLength"
          :maxlength="field.maxLength"
          :pattern="field.pattern"
          @input="handleInput(field.fieldName, $event)"
          class="form-control"
        />

        <ElTextarea
          v-else-if="field.fieldType === 'textarea'"
          :model-value="getFieldValue(field.fieldName)"
          :placeholder="field.placeholder"
          :disabled="disabled"
          :minlength="field.minLength"
          :maxlength="field.maxLength"
          @input="handleInput(field.fieldName, $event)"
          class="form-control"
        />

        <ElInputNumber
          v-else-if="field.fieldType === 'number'"
          :model-value="getFieldValue(field.fieldName)"
          :placeholder="field.placeholder"
          :disabled="disabled"
          :min="field.min"
          :max="field.max"
          @input="handleInput(field.fieldName, $event)"
          class="form-control"
        />

        <ElSelect
          v-else-if="field.fieldType === 'select'"
          :model-value="getFieldValue(field.fieldName)"
          :placeholder="field.placeholder"
          :disabled="disabled"
          @change="handleInput(field.fieldName, $event)"
          class="form-control"
        >
          <ElOption
            v-for="option in field.options"
            :key="option.value"
            :value="option.value"
            :label="option.label"
          />
        </ElSelect>

        <ElRadioGroup
          v-else-if="field.fieldType === 'radio'"
          :model-value="getFieldValue(field.fieldName)"
          :disabled="disabled"
          @change="handleInput(field.fieldName, $event)"
          class="form-control"
        >
          <ElRadio
            v-for="option in field.options"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </ElRadio>
        </ElRadioGroup>

        <ElCheckboxGroup
          v-else-if="field.fieldType === 'checkbox'"
          :model-value="getFieldValue(field.fieldName)"
          :disabled="disabled"
          @change="handleInput(field.fieldName, $event)"
          class="form-control"
        >
          <ElCheckbox
            v-for="option in field.options"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </ElCheckbox>
        </ElCheckboxGroup>

        <ElDatePicker
          v-else-if="field.fieldType === 'date'"
          :model-value="getFieldValue(field.fieldName)"
          :placeholder="field.placeholder"
          :disabled="disabled"
          type="date"
          format="YYYY-MM-DD"
          @update:model-value="handleInput(field.fieldName, $event)"
          class="form-control"
        />

        <ElTimePicker
          v-else-if="field.fieldType === 'time'"
          :model-value="getFieldValue(field.fieldName)"
          :placeholder="field.placeholder"
          :disabled="disabled"
          format="HH:mm:ss"
          @update:model-value="handleInput(field.fieldName, $event)"
          class="form-control"
        />

        <ElSwitch
          v-else-if="field.fieldType === 'switch'"
          :model-value="getFieldValue(field.fieldName)"
          :disabled="disabled"
          @change="handleInput(field.fieldName, $event)"
          class="form-control"
        />

        <ElInput
          v-else-if="field.fieldType === 'password'"
          :model-value="getFieldValue(field.fieldName)"
          :placeholder="field.placeholder"
          :disabled="disabled"
          type="password"
          @input="handleInput(field.fieldName, $event)"
          class="form-control"
        />

        <ElInput
          v-else
          :model-value="getFieldValue(field.fieldName)"
          :placeholder="field.placeholder"
          :disabled="disabled"
          @input="handleInput(field.fieldName, $event)"
          class="form-control"
        />
      </div>
    </template>
  </div>
</template>

<style lang="scss" scoped>
.dynamic-form {
  .form-item {
    margin-bottom: 16px;
  }

  .form-label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
    color: #303133;

    .required-mark {
      color: #f56c6c;
      margin-left: 4px;
    }
  }

  .form-control {
    width: 100%;
  }
}
</style>
