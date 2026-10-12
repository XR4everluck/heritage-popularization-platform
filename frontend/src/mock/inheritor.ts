/**
 * 传承人 mock 数据（列表 + 详情）
 * 字段与详情页 InheritorDetail.vue 的数据形状完全对齐（含 works/heritageList/videos 子列表）。
 */
import { defaultCover } from '../utils/placeholder'

export interface InheritorItem {
  id: number
  name: string
  title: string
  avatar: string
  introduction: string
  experience: string
  achievements: string
  tags: string[]
  works: Array<{ id: number; title: string; image: string; description: string; tags: string[] }>
  heritageList: Array<{ id: number; name: string; coverImage: string; description: string; region: string; level: string }>
  videos: Array<{ id: number; title: string; coverImage: string; description: string; duration: number; viewCount: number }>
}

/** 人物均为虚构演示数据；头像为本地生成的占位图，真实素材就位后替换 */
export const inheritors: InheritorItem[] = [
  {
    id: 1, name: '张同路', title: '国家级代表性传承人', avatar: defaultCover('张同路', 300, 300),
    introduction: '从业四十余年，全面掌握景泰蓝制胎、掐丝、点蓝、烧蓝、磨光、镀金全流程技艺，作品多次获国家级工艺美术金奖。',
    experience: '十六岁进入北京珐琅厂学徒，师从老一辈掐丝艺人；1985年起独立主持大型景泰蓝器物设计与制作；2018年被评为国家级代表性传承人。',
    achievements: '作品《铜胎掐丝珐琅瓶》获中国工艺美术百花奖金奖；主持复原明代宫廷珐琅器形制十余件；带徒三十余人。',
    tags: ['景泰蓝制作技艺', '国家级'],
    works: [
      { id: 1, title: '铜胎掐丝珐琅瓶', image: defaultCover('珐琅瓶', 200, 150), description: '以传统缠枝莲纹为骨架，釉色以蓝为底、赤金勾勒。', tags: ['掐丝', '点蓝'] },
      { id: 2, title: '珐琅炉', image: defaultCover('珐琅炉', 200, 150), description: '仿明宣德炉形制，通体鎏金，炉身饰宝相花。', tags: ['镀金'] }
    ],
    heritageList: [
      { id: 1, name: '景泰蓝制作技艺', coverImage: defaultCover('景泰蓝制作技艺', 200, 150), description: '铜胎掐丝珐琅宫廷工艺。', region: '北京市', level: '国家级' }
    ],
    videos: [
      { id: 2, title: '景泰蓝的百年技艺', coverImage: defaultCover('景泰蓝技艺', 200, 150), description: '跟拍制作全流程。', duration: 38, viewCount: 1954 }
    ]
  },
  {
    id: 2, name: '陈雅韵', title: '国家级代表性传承人', avatar: defaultCover('陈雅韵', 300, 300),
    introduction: '工闺门旦，嗓音清丽婉转，表演细腻传神，长期致力于昆曲进校园推广教学。',
    experience: '毕业于戏曲学校昆剧班，工闺门旦；师承南昆名家；长期在苏州昆剧院从艺并兼任高校昆曲社团指导。',
    achievements: '曾获中国戏曲梅花奖；主演《牡丹亭》全本海内外巡演百余场；录制昆曲普及课程入选国家公共文化云平台。',
    tags: ['昆曲', '国家级'],
    works: [
      { id: 3, title: '牡丹亭·游园', image: defaultCover('牡丹亭', 200, 150), description: '「原来姹紫嫣红开遍」一折的经典演绎。', tags: ['闺门旦'] }
    ],
    heritageList: [
      { id: 2, name: '昆曲', coverImage: defaultCover('昆曲', 200, 150), description: '被誉为"百戏之祖"的古老剧种。', region: '江苏省苏州市', level: '国家级' }
    ],
    videos: [
      { id: 1, title: '昆曲之美：水磨腔入门', coverImage: defaultCover('昆曲之美', 200, 150), description: '系统了解昆曲的审美体系。', duration: 45, viewCount: 2860 }
    ]
  },
  {
    id: 3, name: '沈凤霞', title: '国家级代表性传承人', avatar: defaultCover('沈凤霞', 300, 300),
    introduction: '苏绣艺术家，擅双面绣与人物肖像绣，针法融汇平针、乱针之长。',
    experience: '出身刺绣世家，八岁随母学艺；1980年代进入苏州刺绣研究所；作品多次作为国礼出国访问。',
    achievements: '双面绣《猫》获中国工艺美术大师作品博览会金奖；培养绣娘二百余人。',
    tags: ['苏绣', '国家级'],
    works: [
      { id: 4, title: '双面绣《猫》', image: defaultCover('双面绣猫', 200, 150), description: '正反两面观感一致，毛丝蓬松、双目传神。', tags: ['双面绣'] }
    ],
    heritageList: [
      { id: 5, name: '苏绣', coverImage: defaultCover('苏绣', 200, 150), description: '中国四大名绣之一。', region: '江苏省苏州市', level: '国家级' }
    ],
    videos: [
      { id: 3, title: '一针一线话苏绣', coverImage: defaultCover('苏绣', 200, 150), description: '八字诀里的苏绣密码。', duration: 52, viewCount: 2310 }
    ]
  },
  {
    id: 4, name: '王守拙', title: '国家级代表性传承人', avatar: defaultCover('王守拙', 300, 300),
    introduction: '龙泉青瓷烧制技艺传承人，深耕哥窑、弟窑釉色复原研究三十余年。',
    experience: '陶瓷专业毕业后进入龙泉瓷厂；1990年代创办个人工作室，专注传统龙窑烧制技艺的整理与复原。',
    achievements: '作品《梅子青执壶》被国家博物馆收藏；参与编写《龙泉青瓷烧制技艺》教材。',
    tags: ['龙泉青瓷', '国家级'],
    works: [
      { id: 5, title: '梅子青执壶', image: defaultCover('梅子青执壶', 200, 150), description: '釉色青翠如玉，是弟窑的代表釉色。', tags: ['弟窑'] }
    ],
    heritageList: [
      { id: 6, name: '龙泉青瓷烧制技艺', coverImage: defaultCover('龙泉青瓷', 200, 150), description: '全球唯一入选人类非遗的陶瓷类项目。', region: '浙江省龙泉市', level: '国家级' }
    ],
    videos: [
      { id: 4, title: '青瓷开窑：龙泉烧制实录', coverImage: defaultCover('龙泉青瓷', 200, 150), description: '记录一次完整的开窑过程。', duration: 60, viewCount: 1580 }
    ]
  },
  {
    id: 5, name: '韩菊香', title: '省级代表性传承人', avatar: defaultCover('韩菊香', 300, 300),
    introduction: '蔚县剪纸艺人，专攻戏曲脸谱刻制与点彩，刻刀下人物须眉毕现。',
    experience: '自幼随父学习刻纸；深耕阴刻点彩技法；作品多次参加全国剪纸展并获奖。',
    achievements: '《戏曲脸谱》系列获中国民间文艺山花奖；进校园授课累计百余课时。',
    tags: ['蔚县剪纸', '省级'],
    works: [
      { id: 6, title: '戏曲脸谱系列', image: defaultCover('戏曲脸谱', 200, 150), description: '生旦净丑，刀刀见功。', tags: ['阴刻', '点彩'] }
    ],
    heritageList: [
      { id: 3, name: '中国剪纸（蔚县剪纸）', coverImage: defaultCover('蔚县剪纸', 200, 150), description: '唯一以阴刻为主的剪纸流派。', region: '河北省张家口市', level: '国家级' }
    ],
    videos: []
  },
  {
    id: 6, name: '欧阳松', title: '省级代表性传承人', avatar: defaultCover('欧阳松', 300, 300),
    introduction: '苗族银饰锻制技艺传承人，能独立完成银角、银冠等大型头饰的锻制。',
    experience: '出身银匠世家，掌握拉丝、錾刻等核心工序；常年往返于黔东南各村寨收购老银饰并整理纹样图谱。',
    achievements: '复原传统银饰纹样一百二十余种；作品入选中国非物质文化遗产博览会展示。',
    tags: ['苗族银饰', '省级'],
    works: [
      { id: 7, title: '银角', image: defaultCover('银角', 200, 150), description: '雷山苗寨盛装头饰，角上铸有龙纹。', tags: ['锻制'] }
    ],
    heritageList: [
      { id: 11, name: '苗族银饰锻制技艺', coverImage: defaultCover('苗族银饰', 200, 150), description: '"穿在身上的史诗"。', region: '贵州省黔东南州', level: '国家级' }
    ],
    videos: []
  }
]

export function list(): InheritorItem[] {
  return inheritors
}

export function detail(id: number): InheritorItem | null {
  return inheritors.find((i) => i.id === Number(id)) ?? null
}
