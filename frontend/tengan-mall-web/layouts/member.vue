<template>
  <div class="min-h-screen flex flex-col bg-gray-100">
    <LayoutAppHeader />
    <div class="max-w-7xl mx-auto w-full flex-1 flex items-start gap-6 px-6 py-6">
      <MemberSidebar />
      <div class="flex-1 min-w-0 bg-white rounded-lg p-6">
        <slot />
      </div>
    </div>
    <LayoutAppFooter />
  </div>
</template>
