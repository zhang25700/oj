<template>
  <div ref="containerRef" class="code-editor"></div>
</template>

<script setup>
import * as monaco from "monaco-editor";
import { onBeforeUnmount, onMounted, ref, watch } from "vue";

const props = defineProps({
  modelValue: {
    type: String,
    default: ""
  },
  language: {
    type: String,
    default: "java"
  }
});

const emit = defineEmits(["update:modelValue", "submit"]);
const containerRef = ref(null);
let editor;
let syncing = false;

function resolveLanguage(language) {
  if (language === "cpp" || language === "c") {
    return "cpp";
  }
  return "java";
}

onMounted(() => {
  editor = monaco.editor.create(containerRef.value, {
    value: props.modelValue,
    language: resolveLanguage(props.language),
    theme: "vs-dark",
    automaticLayout: true,
    minimap: { enabled: false },
    fontSize: 14,
    scrollBeyondLastLine: false,
    tabSize: 2,
    insertSpaces: true
  });

  editor.onDidChangeModelContent(() => {
    if (syncing) {
      return;
    }
    emit("update:modelValue", editor.getValue());
  });

  editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.Enter, () => {
    emit("submit");
  });
});

watch(
  () => props.modelValue,
  (value) => {
    if (!editor || editor.getValue() === value) {
      return;
    }
    syncing = true;
    editor.setValue(value);
    syncing = false;
  }
);

watch(
  () => props.language,
  (language) => {
    if (!editor) {
      return;
    }
    const model = editor.getModel();
    if (model) {
      monaco.editor.setModelLanguage(model, resolveLanguage(language));
    }
  }
);

onBeforeUnmount(() => {
  if (editor) {
    editor.dispose();
  }
});
</script>
