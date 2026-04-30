/**
 * Utility to extract device and system information from the browser environment.
 */

export interface SystemMetrics {
  os: {
    name: string;
    version: string;
    platform: string;
  };
  hardware: {
    cpuCores: number;
    memoryLimit: string;
    architecture: string;
  };
  display: {
    resolution: string;
    refreshRate: number;
    colorDepth: string;
    pixelRatio: number;
  };
  network: {
    online: boolean;
    type: string;
    effectiveType: string;
    downlink: string;
  };
  environment: {
    language: string;
    timezone: string;
    userAgent: string;
  };
}

export const getSystemInfo = (): SystemMetrics => {
  const ua = navigator.userAgent;
  
  // Basic OS detection from User Agent
  let osName = 'Unknown';
  let osVersion = 'Unknown';
  let deviceType = 'Desktop';
  
  if (ua.indexOf('Android') > -1) {
    if (ua.indexOf('TV') > -1 || ua.indexOf('GoogleTV') > -1 || ua.indexOf('AFTB') > -1 || ua.indexOf('ATV') > -1) {
      osName = 'Android TV';
      deviceType = 'Smart TV';
    } else {
      osName = 'Android';
      deviceType = 'Mobile/Tablet';
    }
    const match = ua.match(/Android\s([0-9\.]+)/);
    if (match) osVersion = match[1];
  } else if (ua.indexOf('CrOS') > -1) {
    osName = 'ChromeOS';
    deviceType = 'Laptop';
  } else if (ua.indexOf('Tizen') > -1) {
    osName = 'Tizen OS';
    deviceType = 'Smart TV';
  } else if (ua.indexOf('Web0S') > -1 || ua.indexOf('webOS') > -1) {
    osName = 'webOS';
    deviceType = 'Smart TV';
  } else if (ua.indexOf('Win') > -1) {
    osName = 'Windows';
  } else if (ua.indexOf('Mac') > -1) {
    osName = 'macOS';
  }

  // Network info
  const conn = (navigator as any).connection || (navigator as any).mozConnection || (navigator as any).webkitConnection;

  // Browser info
  const browserMatch = ua.match(/(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*(\d+)/i) || [];
  const browserName = browserMatch[1] || 'Unknown';
  const browserVersion = browserMatch[2] || 'Unknown';

  return {
    os: {
      name: osName,
      version: osVersion,
      platform: (navigator as any).userAgentData?.platform || navigator.platform,
    },
    hardware: {
      cpuCores: navigator.hardwareConcurrency || 0,
      //@ts-ignore
      memoryLimit: navigator.deviceMemory ? `${navigator.deviceMemory} GB` : '8 GB (Estimated)',
      architecture: (navigator as any).userAgentData?.architecture || 'ARM/x64',
    },
    display: {
      resolution: `${window.screen.width} × ${window.screen.height}`,
      refreshRate: (window as any).screen?.refreshRate || 60,
      colorDepth: `${window.screen.colorDepth}-bit`,
      pixelRatio: window.devicePixelRatio,
    },
    network: {
      online: navigator.onLine,
      type: conn?.type || 'WiFi/Ethernet',
      effectiveType: conn?.effectiveType || '4g',
      downlink: conn?.downlink ? `${conn.downlink} Mbps` : '100 Mbps (Est.)',
    },
    environment: {
      language: navigator.language,
      timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
      userAgent: ua,
    },
  };
};
