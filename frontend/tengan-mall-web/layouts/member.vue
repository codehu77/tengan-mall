<template>
  <div class="min-h-screen flex flex-col bg-[#F6F7F9]">
    <LayoutAppHeader />
    <div class="max-w-7xl mx-auto w-full flex-1 flex items-start gap-6 px-6 py-6">
      <MemberSidebar />
      <div class="flex-1 min-w-0 bg-white rounded-2xl border border-gray-100 p-8">
        <slot />
      </div>
    </div>
    <LayoutAppFooter />
  </div>
</template>
