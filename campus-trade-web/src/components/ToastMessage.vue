<template>
  <div v-if="message"
       class="toast"
       :class="type">
    {{message}}
  </div>
</template>

<script setup>
defineProps({
  message:String,
  type:String
})
</script>

