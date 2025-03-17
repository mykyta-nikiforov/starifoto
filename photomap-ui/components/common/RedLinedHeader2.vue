<template>
  <h2 class="red-lined-header-2"><span class="headline">
    <slot></slot>
</span></h2>
</template>

<style scoped lang="less">
.red-lined-header-2 {
  border-top: 1px solid #D1D5DB;
  padding-top: 2rem;

  @media (min-width: 768px) {
    padding-top: 2.5rem;
  }
}
.headline {
  box-shadow: inset 3px 0 0 #B64038;
  padding-left: 2rem;
  display: inline-block;
}
</style>