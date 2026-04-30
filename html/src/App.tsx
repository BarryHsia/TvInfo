import { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import {
  Cpu,
  Monitor,
  Network,
  Info,
  Smartphone,
  Globe,
  Settings,
  Activity,
  Zap,
  HardDrive,
  Shield,
  Clock,
  Wifi,
  Database,
  Layers,
  ChevronRight,
  Maximize2
} from 'lucide-react';
import { getSystemInfo, SystemMetrics } from './lib/device.ts';

const NAV_ITEMS = [
  { id: 'overview', label: 'Overview', icon: Zap, color: 'text-cyan-400' },
  { id: 'hardware', label: 'Hardware', icon: Cpu, color: 'text-orange-400' },
  { id: 'display', label: 'Display', icon: Monitor, color: 'text-purple-400' },
  { id: 'network', label: 'Network', icon: Wifi, color: 'text-emerald-400' },
  { id: 'software', label: 'Software', icon: Shield, color: 'text-blue-400' },
  { id: 'storage', label: 'Storage', icon: Database, color: 'text-rose-400' },
];

export default function App() {
  const [metrics, setMetrics] = useState<SystemMetrics | null>(null);
  const [activeTab, setActiveTab] = useState('overview');
  const [currentTime, setCurrentTime] = useState(new Date());
  
  useEffect(() => {
    setMetrics(getSystemInfo());
    const timer = setInterval(() => setCurrentTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  if (!metrics) return null;

  const timeString = currentTime.toLocaleTimeString([], {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  });

  const dateString = currentTime.toLocaleDateString([], {
    weekday: 'long',
    month: 'short',
    day: 'numeric',
  });

  const renderContent = () => {
    switch (activeTab) {
      case 'overview':
        return (
          <motion.div 
            key="overview"
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            className="grid grid-cols-6 grid-rows-2 gap-4 h-full"
          >
            <div className="bento-card col-span-4 row-span-1 flex flex-col justify-between group tv-focus" tabIndex={0}>
              <div className="flex justify-between items-start">
                <div>
                  <p className="stat-label">Device Profile</p>
                  <h2 className="text-4xl font-bold text-white tracking-tight leading-none mt-2">
                    {metrics.os.name} Device
                  </h2>
                  <p className="text-cyan-400 font-mono text-[10px] mt-2 uppercase tracking-[0.3em]">
                    Build Version {metrics.os.version || "12.0.1"} // Stable
                  </p>
                </div>
                <div className="w-16 h-16 rounded-2xl bg-white/5 flex items-center justify-center border border-white/10">
                  <Smartphone className="text-white/20 group-focus:text-cyan-400 transition-colors" size={32} />
                </div>
              </div>
              <div className="flex gap-8 mt-auto pt-8 border-t border-white/5">
                <div>
                  <p className="stat-label">Serial</p>
                  <p className="font-mono text-sm text-gray-400 uppercase">TV-X88G2-JS</p>
                </div>
                <div>
                  <p className="stat-label">Region</p>
                  <p className="font-mono text-sm text-gray-400 uppercase">{metrics.environment.timezone.split('/')[1] || 'Global'}</p>
                </div>
                <div>
                  <p className="stat-label">Language</p>
                  <p className="font-mono text-sm text-gray-400 uppercase">{metrics.environment.language}</p>
                </div>
              </div>
            </div>

            <div className="bento-card col-span-2 row-span-1 flex flex-col justify-between group tv-focus" tabIndex={0}>
              <div className="flex justify-between">
                <p className="stat-label text-orange-400">Processor</p>
                <Cpu size={20} className="text-orange-400/50" />
              </div>
              <div>
                <h3 className="text-3xl font-bold text-white">{metrics.hardware.cpuCores} Cores</h3>
                <p className="text-xs text-gray-500 font-mono mt-1 opacity-70">Hyper-Threading Active</p>
              </div>
              <div className="h-1 bg-white/5 rounded-full overflow-hidden mt-4">
                <motion.div 
                  initial={{ width: 0 }}
                  animate={{ width: '42%' }}
                  className="h-full bg-orange-500"
                />
              </div>
            </div>

            <div className="bento-card col-span-2 row-span-1 flex flex-col justify-between group tv-focus" tabIndex={0}>
              <div className="flex justify-between">
                <p className="stat-label text-purple-400">Memory</p>
                <Activity size={20} className="text-purple-400/50" />
              </div>
              <div>
                <h3 className="text-3xl font-bold text-white">{metrics.hardware.memoryLimit}</h3>
                <p className="text-xs text-gray-500 font-mono mt-1">DDR4 Memory Pool</p>
              </div>
              <div className="flex justify-between items-center mt-4">
                 <span className="text-[10px] font-mono text-gray-600 uppercase">Utilized</span>
                 <span className="text-xs font-mono text-purple-400">3.2 GB Free</span>
              </div>
            </div>

            <div className="bento-card col-span-4 row-span-1 flex flex-col justify-between group tv-focus" tabIndex={0}>
              <div className="flex justify-between items-start">
                <div>
                  <p className="stat-label text-emerald-400">Connectivity</p>
                  <h3 className="text-3xl font-bold text-white mt-1 uppercase tracking-tight">LINK_ESTABLISHED</h3>
                </div>
                <div className="flex items-center gap-2 px-3 py-1 bg-emerald-500/10 border border-emerald-500/20 rounded-full">
                  <div className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
                  <span className="text-[10px] font-mono text-emerald-400 uppercase font-bold tracking-widest whitespace-nowrap">Online</span>
                </div>
              </div>
              
              <div className="grid grid-cols-3 gap-4 mt-8">
                <div>
                  <p className="stat-label">Downlink</p>
                  <p className="text-xl font-mono text-white">{metrics.network.downlink}</p>
                </div>
                <div>
                  <p className="stat-label">Type</p>
                  <p className="text-xl font-mono text-white uppercase">{metrics.network.type}</p>
                </div>
                <div>
                  <p className="stat-label">Protocol</p>
                  <p className="text-xl font-mono text-white">IPv4/v6</p>
                </div>
              </div>
            </div>
          </motion.div>
        );
      case 'hardware':
        return (
          <motion.div 
            key="hardware"
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            className="space-y-4"
          >
            <div className="bento-card flex gap-8 items-center bg-gradient-to-br from-orange-500/5 to-transparent border-orange-500/10">
              <div className="w-20 h-20 bg-orange-500/10 rounded-3xl flex items-center justify-center text-orange-400 border border-orange-500/20">
                <Cpu size={40} strokeWidth={1} />
              </div>
              <div className="flex-1">
                <p className="stat-label text-orange-400">Main Processor (SoC)</p>
                <h3 className="text-2xl font-bold text-white">System Architecture: {metrics.hardware.architecture}</h3>
                <p className="text-sm text-gray-500 font-mono mt-1">Multi-core processing unit with integrated NPU</p>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="bento-card">
                <p className="stat-label">CPU Features</p>
                <div className="space-y-3 mt-4">
                  {['64-bit ARM v8.2', 'Advanced Neon', 'VFP v4', 'AES Hardware'].map(feat => (
                    <div key={feat} className="flex items-center gap-3 text-xs font-mono text-gray-300">
                      <ChevronRight size={14} className="text-orange-500" />
                      {feat}
                    </div>
                  ))}
                </div>
              </div>
              <div className="bento-card">
                <p className="stat-label">RAM Config</p>
                <div className="space-y-4 mt-4">
                  <div className="flex justify-between text-xs font-mono">
                    <span className="text-gray-500">Total Installed</span>
                    <span className="text-white">{metrics.hardware.memoryLimit}</span>
                  </div>
                  <div className="flex justify-between text-xs font-mono">
                    <span className="text-gray-500">Configured Clock</span>
                    <span className="text-white">1866 MHz</span>
                  </div>
                  <div className="flex justify-between text-xs font-mono">
                    <span className="text-gray-500">Virtual RAM</span>
                    <span className="text-rose-400">Disabled</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="bento-card group tv-focus" tabIndex={0}>
              <div className="flex justify-between items-center mb-6">
                <p className="stat-label">Instruction Set Support</p>
                <Layers size={18} className="text-gray-600" />
              </div>
              <div className="flex flex-wrap gap-2">
                {['arm64-v8a', 'armeabi-v7a', 'armeabi'].map(isa => (
                  <span key={isa} className="px-3 py-1 rounded-lg bg-white/5 border border-white/10 text-[10px] font-mono text-gray-400 uppercase tracking-widest">{isa}</span>
                ))}
              </div>
            </div>
          </motion.div>
        );
      case 'display':
        return (
          <motion.div 
            key="display"
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            className="grid grid-cols-4 gap-4"
          >
            <div className="bento-card col-span-3 flex flex-col justify-between">
              <div className="flex justify-between items-start">
               <div>
                  <p className="stat-label text-purple-400">Current Resolution</p>
                  <h3 className="text-5xl font-bold text-white tracking-tighter mt-1">{metrics.display.resolution}</h3>
               </div>
               <div className="w-12 h-12 bg-purple-500/10 rounded-xl flex items-center justify-center text-purple-400 border border-purple-500/20 shadow-[0_0_20px_rgba(168,85,247,0.2)]">
                  <Maximize2 size={24} />
               </div>
              </div>
              <div className="flex gap-4 mt-8">
                {['HDR10+', 'HLG', 'V-Sync', 'BT2020'].map(tag => (
                   <span key={tag} className="px-2 py-1 rounded bg-purple-500/10 text-purple-400 border border-purple-500/20 text-[9px] font-mono font-bold tracking-[0.2em]">{tag}</span>
                ))}
              </div>
            </div>

            <div className="bento-card col-span-1 flex flex-col justify-center items-center text-center">
              <p className="stat-label">Refresh</p>
              <h3 className="text-4xl font-bold text-white mt-1">{metrics.display.refreshRate}Hz</h3>
              <p className="text-[10px] font-mono text-gray-500 uppercase mt-2">Smooth Motion</p>
            </div>

            <div className="bento-card col-span-2">
              <p className="stat-label">Color Space</p>
              <div className="space-y-4 mt-4">
                <div className="flex justify-between items-center">
                   <span className="text-xs font-mono text-gray-400">Bit Depth</span>
                   <span className="text-xs font-mono text-white">{metrics.display.colorDepth}</span>
                </div>
                <div className="flex justify-between items-center">
                   <span className="text-xs font-mono text-gray-400">Pixel Ratio</span>
                   <span className="text-xs font-mono text-white">{metrics.display.pixelRatio}x</span>
                </div>
              </div>
            </div>

            <div className="bento-card col-span-2">
              <p className="stat-label">Orientation</p>
              <p className="text-2xl font-bold text-white mt-2">Landscape (Primary)</p>
              <p className="text-[10px] font-mono text-gray-500 uppercase mt-1">Locked by TV Hardware</p>
            </div>
          </motion.div>
        );
      case 'network':
        return (
          <motion.div 
            key="network"
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            className="space-y-4"
          >
             <div className="bento-card bg-emerald-500/5 border-emerald-500/10">
               <div className="flex items-center gap-6 mb-8">
                  <div className="w-16 h-16 bg-emerald-500/10 rounded-2xl flex items-center justify-center text-emerald-400 border border-emerald-500/20 shadow-[0_0_30px_rgba(16,185,129,0.15)]">
                    <Wifi size={32} />
                  </div>
                  <div>
                    <h3 className="text-2xl font-bold text-white tracking-tight">Active Interface: {metrics.network.type}</h3>
                    <p className="text-xs font-mono text-emerald-400/70 tracking-widest uppercase">Connection Peer: Verified</p>
                  </div>
               </div>
               
               <div className="grid grid-cols-4 gap-4">
                  <div className="space-y-1">
                    <p className="stat-label">IP_ADDR</p>
                    <p className="font-mono text-white">192.168.1.102</p>
                  </div>
                  <div className="space-y-1">
                    <p className="stat-label">GATEWAY</p>
                    <p className="font-mono text-white">192.168.1.1</p>
                  </div>
                  <div className="space-y-1">
                    <p className="stat-label">DNS_1</p>
                    <p className="font-mono text-white">8.8.8.8</p>
                  </div>
                  <div className="space-y-1">
                    <p className="stat-label">MTU_SIZE</p>
                    <p className="font-mono text-white">1500</p>
                  </div>
               </div>
             </div>

             <div className="grid grid-cols-2 gap-4">
                <div className="bento-card">
                  <p className="stat-label">Signal Integrity</p>
                  <div className="flex items-end gap-1 mt-4 mb-2 h-12">
                    {[30, 60, 45, 90, 80].map((h, i) => (
                      <motion.div 
                        key={i}
                        initial={{ height: 0 }}
                        animate={{ height: `${h}%` }}
                        className="flex-1 bg-emerald-500/40 rounded-t-sm"
                      />
                    ))}
                  </div>
                  <div className="flex justify-between items-center text-[10px] font-mono text-gray-500 uppercase tracking-widest">
                    <span>Low</span>
                    <span className="text-emerald-400 font-bold">Stable</span>
                    <span>High</span>
                  </div>
                </div>
                <div className="bento-card flex flex-col justify-between">
                   <p className="stat-label">MAC Address</p>
                   <h4 className="text-xl font-mono text-white break-all">00:1A:2B:3C:4D:5E</h4>
                   <p className="text-[10px] font-mono text-gray-600 uppercase mt-2">Physical Hardware ID</p>
                </div>
             </div>
          </motion.div>
        );
      case 'software':
        return (
          <motion.div 
            key="software"
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            className="space-y-4"
          >
            <div className="bento-card flex items-center gap-6">
              <div className="w-16 h-16 bg-blue-500/10 rounded-2xl flex items-center justify-center text-blue-400 border border-blue-500/20">
                <Shield size={32} />
              </div>
              <div className="flex-1">
                <p className="stat-label text-blue-400">Environment Details</p>
                <h3 className="text-2xl font-bold text-white">Build Signature: STTE.231215.005</h3>
                <p className="text-xs font-mono text-gray-500 mt-1 uppercase tracking-widest">{metrics.os.platform} // X8_STATION</p>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="bento-card">
                <p className="stat-label">Core Runtime</p>
                <div className="space-y-4 mt-4">
                  <div className="flex justify-between text-xs font-mono">
                    <span className="text-gray-500">Android API</span>
                    <span className="text-white">Level 31</span>
                  </div>
                  <div className="flex justify-between text-xs font-mono">
                    <span className="text-gray-500">OpenGLES</span>
                    <span className="text-white">v3.2</span>
                  </div>
                  <div className="flex justify-between text-xs font-mono">
                    <span className="text-gray-500">Vulkan Support</span>
                    <span className="text-emerald-400 font-bold">YES</span>
                  </div>
                </div>
              </div>
              <div className="bento-card">
                <p className="stat-label">Security</p>
                <div className="space-y-4 mt-4">
                  <div className="flex justify-between text-xs font-mono">
                    <span className="text-gray-500">S-Patch</span>
                    <span className="text-white">April 2026</span>
                  </div>
                  <div className="flex justify-between text-xs font-mono">
                    <span className="text-gray-500">SELinux</span>
                    <span className="text-blue-400">Enforcing</span>
                  </div>
                  <div className="flex justify-between text-xs font-mono">
                    <span className="text-gray-500">Bootloader</span>
                    <span className="text-orange-400">Locked</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="bento-card bg-white/[0.01]">
              <p className="stat-label mb-4 uppercase">Kernel Identity</p>
              <div className="p-4 rounded-xl bg-black/40 border border-white/5 font-mono text-[10px] text-blue-400/70 leading-relaxed break-all">
                Linux version 5.10.168-android12-9-g12345678 (gcc version 10.2.1) #1 SMP PREEMPT Thu Dec 14 12:45:10 UTC 2023
              </div>
            </div>
          </motion.div>
        );
      case 'storage':
        return (
          <motion.div 
            key="storage"
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            className="space-y-4"
          >
            <div className="bento-card">
               <div className="flex justify-between items-end mb-6">
                 <div>
                   <p className="stat-label text-rose-400">Internal Storage</p>
                   <h3 className="text-4xl font-bold text-white mt-1">12.4 GB / 16.0 GB</h3>
                 </div>
                 <div className="text-right">
                    <p className="text-[10px] font-mono text-gray-500 uppercase">Usage Status</p>
                    <p className="text-xl font-bold text-rose-400">78% Full</p>
                 </div>
               </div>
               <div className="h-4 bg-white/5 rounded-full overflow-hidden flex">
                  <motion.div initial={{ width: 0 }} animate={{ width: '45%' }} className="h-full bg-rose-500 border-r border-rose-600/30" />
                  <motion.div initial={{ width: 0 }} animate={{ width: '20%' }} className="h-full bg-orange-500/60 border-r border-orange-600/30" />
                  <motion.div initial={{ width: 0 }} animate={{ width: '13%' }} className="h-full bg-gray-600" />
               </div>
               <div className="grid grid-cols-4 gap-4 mt-8">
                  <div className="flex items-center gap-3">
                    <div className="w-2 h-2 rounded-full bg-rose-500" />
                    <span className="text-[10px] font-mono text-gray-400 uppercase">System OS</span>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="w-2 h-2 rounded-full bg-orange-500/60" />
                    <span className="text-[10px] font-mono text-gray-400 uppercase">Installed Apps</span>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="w-2 h-2 rounded-full bg-gray-600" />
                    <span className="text-[10px] font-mono text-gray-400 uppercase">Temp Cache</span>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="w-2 h-2 rounded-full bg-white/10" />
                    <span className="text-[10px] font-mono text-gray-400 uppercase">Available</span>
                  </div>
               </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="bento-card group tv-focus" tabIndex={0}>
                <div className="flex justify-between items-center mb-4">
                  <p className="stat-label">External Expansion</p>
                  <HardDrive size={18} className="text-gray-600" />
                </div>
                <h4 className="text-lg font-bold text-gray-300">No USB Drive Detected</h4>
                <p className="text-xs text-gray-600 font-mono mt-1 uppercase">Mount point: /mnt/media_rw/</p>
              </div>
              <div className="bento-card group tv-focus" tabIndex={0}>
                <div className="flex justify-between items-center mb-4">
                  <p className="stat-label">File System</p>
                  <Database size={18} className="text-gray-600" />
                </div>
                <h4 className="text-lg font-bold text-gray-300">Format: EXT4 / F2FS</h4>
                <p className="text-xs text-gray-600 font-mono mt-1 uppercase">Encryption: FBE (Enabled)</p>
              </div>
            </div>
          </motion.div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="flex h-screen bg-[#030406] overflow-hidden text-gray-400 font-sans selection:bg-cyan-500/30">
      <div className="fixed inset-0 pointer-events-none -z-10 bg-mesh opacity-40" />
      <div className="fixed top-0 left-0 w-full h-[600px] bg-gradient-to-b from-cyan-500/5 to-transparent pointer-events-none -z-10" />

      <nav className="w-80 flex flex-col pt-16 border-r border-white/5 bg-black/20 backdrop-blur-3xl z-10 shrink-0">
        <div className="px-12 mb-16">
          <div className="flex items-center gap-4 group">
            <div className="w-14 h-14 bg-cyan-500 rounded-2xl flex items-center justify-center text-black shadow-[0_0_40px_rgba(34,211,238,0.3)] transition-transform group-hover:scale-105">
              <Settings size={30} strokeWidth={2.5} />
            </div>
            <div>
              <h1 className="text-2xl font-black text-white tracking-tighter leading-none">TV-SYS</h1>
              <p className="text-[10px] font-mono text-cyan-400 uppercase tracking-[0.3em] font-bold mt-1">Diagnosis // v1.0</p>
            </div>
          </div>
        </div>

        <div className="flex flex-col px-4 gap-2 h-full overflow-y-auto custom-scrollbar pb-20">
          {NAV_ITEMS.map((item) => (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`
                group relative flex items-center gap-5 px-8 py-5 rounded-2xl transition-all duration-300 text-left
                ${activeTab === item.id 
                  ? 'bg-white/10 text-white shadow-2xl ring-1 ring-white/10' 
                  : 'text-gray-500 hover:text-gray-300 hover:bg-white/[0.03]'}
              `}
            >
              {activeTab === item.id && (
                <motion.div 
                  layoutId="active-indicator"
                  className="absolute left-2 w-1.5 h-8 bg-cyan-400 rounded-full"
                  transition={{ type: 'spring', stiffness: 300, damping: 30 }}
                />
              )}
              <item.icon 
                size={24} 
                strokeWidth={activeTab === item.id ? 2 : 1.5}
                className={activeTab === item.id ? item.color : 'opacity-30 group-hover:opacity-100 transition-opacity'} 
              />
              <span className={`text-xl font-bold tracking-tight ${activeTab === item.id ? 'translate-x-0' : 'group-hover:translate-x-1'} transition-transform`}>
                {item.label}
              </span>
            </button>
          ))}
          
          <div className="mt-auto px-8 pb-12">
            <div className="p-6 rounded-3xl bg-white/[0.03] border border-white/[0.06] flex items-center gap-4">
              <div className="w-10 h-10 rounded-xl bg-emerald-500/10 flex items-center justify-center text-emerald-500">
                <Shield size={20} />
              </div>
              <div>
                <p className="text-[9px] font-mono text-gray-500 uppercase tracking-widest">System Health</p>
                <p className="text-xs font-bold text-white">Optimal</p>
              </div>
            </div>
          </div>
        </div>
      </nav>

      <main className="flex-1 flex flex-col min-w-0">
        <header className="px-16 pt-16 pb-12 flex justify-between items-start shrink-0">
          <div>
            <div className="flex items-center gap-3">
              <div className="w-2 h-2 rounded-full bg-cyan-500 animate-pulse" />
              <p className="text-[11px] font-mono text-gray-500 uppercase tracking-[0.4em] font-bold">Remote Node Access</p>
            </div>
            <h2 className="text-2xl font-mono text-gray-400 mt-2 tracking-widest opacity-60">
              {metrics.environment.timezone.split('/')[1] || 'STATION'} // <span className="text-cyan-500/50">ALPHA_UNIT</span>
            </h2>
          </div>
          
          <div className="text-right">
            <div className="flex items-center justify-end gap-3 mb-1 opacity-80">
              <Clock size={16} className="text-cyan-400" />
              <span className="text-[10px] font-mono text-cyan-400 uppercase tracking-widest font-bold">Real-time Clock</span>
            </div>
            <h2 className="text-6xl font-black text-white tracking-tighter leading-none tabular-nums">
              {timeString}
            </h2>
            <p className="text-[11px] font-mono text-gray-500 uppercase tracking-[0.5em] mt-3 font-medium">
              {dateString}
            </p>
          </div>
        </header>

        <section className="flex-1 px-16 pb-16 overflow-y-auto custom-scrollbar flex flex-col justify-start">
          <div className="max-w-5xl w-full">
            <AnimatePresence mode="wait">
               {renderContent()}
            </AnimatePresence>
          </div>
        </section>

        <footer className="h-14 bg-black/40 border-t border-white/5 px-16 flex items-center justify-between shrink-0">
          <div className="flex gap-8">
            <div className="flex items-center gap-2">
              <div className="w-1.5 h-1.5 rounded-full bg-cyan-500" />
              <span className="text-[10px] font-mono text-gray-500 uppercase tracking-widest">Engine Active</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-1.5 h-1.5 rounded-full bg-emerald-500" />
              <span className="text-[10px] font-mono text-gray-500 uppercase tracking-widest">Secured Node</span>
            </div>
          </div>
          <div className="text-[10px] font-mono text-gray-600 uppercase tracking-[0.3em]">
             Authorized System Console // PRD-X880
          </div>
        </footer>
      </main>
    </div>
  );
}
