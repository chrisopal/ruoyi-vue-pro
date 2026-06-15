const MOJIBAKE_RE = /å|æ|è|é|Â|Ã|�/

type BackendMenu = AppCustomRouteRecordRaw & {
  permission?: string
  children?: BackendMenu[]
}

type SanitizedBackendMenu = BackendMenu & {
  __insideLab?: boolean
}

const isMojibakeName = (name?: string) => MOJIBAKE_RE.test(name || '')

const getMenuId = (menu: BackendMenu) => Number(menu.id || Number.MAX_SAFE_INTEGER)

const buildDuplicateKey = (menu: SanitizedBackendMenu) => {
  if (menu.permission) {
    return `permission:${menu.permission}`
  }
  if (menu.__insideLab) {
    return `lab-path:${menu.path || ''}:${menu.component || ''}:${menu.componentName || ''}`
  }
  return `id:${menu.id || menu.path || menu.name}`
}

const pickCanonicalMenu = (menus: SanitizedBackendMenu[]) => {
  return menus
    .slice()
    .sort((left, right) => {
      const leftMojibake = isMojibakeName(left.name) ? 1 : 0
      const rightMojibake = isMojibakeName(right.name) ? 1 : 0
      if (leftMojibake !== rightMojibake) {
        return leftMojibake - rightMojibake
      }
      return getMenuId(left) - getMenuId(right)
    })[0]
}

const stripInternalFields = (menu: SanitizedBackendMenu): BackendMenu => {
  const { __insideLab, children, ...rest } = menu
  return {
    ...rest,
    ...(children?.length ? { children: children.map(stripInternalFields) } : {})
  }
}

const sanitizeLevel = (menus: BackendMenu[] = [], insideLab = false): SanitizedBackendMenu[] => {
  const grouped = new Map<string, SanitizedBackendMenu[]>()

  menus.forEach((menu) => {
    const currentInsideLab = insideLab || menu.path === '/lab'
    const sanitized: SanitizedBackendMenu = {
      ...menu,
      __insideLab: currentInsideLab,
      children: sanitizeLevel(menu.children || [], currentInsideLab)
    }
    const key = buildDuplicateKey(sanitized)
    const group = grouped.get(key) || []
    group.push(sanitized)
    grouped.set(key, group)
  })

  const result: SanitizedBackendMenu[] = []
  grouped.forEach((group) => {
    const canonical = pickCanonicalMenu(group)
    const mergedChildren = group.flatMap((menu) => menu.children || [])

    if (canonical.__insideLab && isMojibakeName(canonical.name)) {
      return
    }

    result.push({
      ...canonical,
      children: mergedChildren.length ? sanitizeLevel(mergedChildren, canonical.__insideLab) : []
    })
  })

  return result.sort((left, right) => {
    const leftSort = Number((left as any).sort ?? 0)
    const rightSort = Number((right as any).sort ?? 0)
    if (leftSort !== rightSort) {
      return leftSort - rightSort
    }
    return getMenuId(left) - getMenuId(right)
  })
}

export const sanitizeMenuRouters = (menus?: AppCustomRouteRecordRaw[]) => {
  return sanitizeLevel((menus || []) as BackendMenu[]).map(stripInternalFields) as AppCustomRouteRecordRaw[]
}

