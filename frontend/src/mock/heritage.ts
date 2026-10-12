/**
 * 非遗项目 mock 数据（项目/分类/详情/快讯/公告/课程/评论/测验/收藏/进度）
 * docs/11 第 6.2 节：mock 与业务代码分离；字段结构与 docs/08 接口文档对齐，
 * 后续对接 SpringBoot 时仅替换 api 层数据源，页面零改动。
 */
import { defaultCover } from '../utils/placeholder'
import { galleriesByHeritageId, coverFor } from './heritage-images'
import { approvedNews, approvedCourses } from './submissions'
import type { GalleryImage } from '../types/models'

// ---------- 分类（十大门类） ----------
export const categories = [
  { id: 1, name: '民间文学' },
  { id: 2, name: '传统音乐' },
  { id: 3, name: '传统舞蹈' },
  { id: 4, name: '传统戏剧' },
  { id: 5, name: '曲艺' },
  { id: 6, name: '传统体育、游艺与杂技' },
  { id: 7, name: '传统美术' },
  { id: 8, name: '传统技艺' },
  { id: 9, name: '传统医药' },
  { id: 10, name: '民俗' }
]

// ---------- 非遗项目 ----------
export interface HeritageItem {
  id: number
  name: string
  level: string
  categoryId: number
  categoryName: string
  region: string
  viewCount: number
  coverImage: string
  summary: string
  originAge: string
  distributionArea: string
  representativeWorks: string
  inheritor: string
  inheritorId: number | null
  endangerLevel: string
  publishTime: string
  /** 是否科普快讯类条目（列表页 isNews=1 筛选用） */
  isNews: number
  /** 图文详情正文（富文本，详情页 .rich-content 渲染） */
  content: string
  /** 详情图集（PhotoGallery 数据；素材未就位时为空数组，组件显示占位） */
  images: GalleryImage[]
}

/**
 * 各项目专属文化内容（历史渊源 / 主要特征），按 id 定制；
 * 详情正文据此生成，保证每个项目内容不重复、可支撑答辩讲解。
 */
const CULTURE_NOTES: Record<number, { history: string; feature: string }> = {
  1: { history: '明代宗室与宫廷对珐琅器的需求催生了铜胎掐丝工艺，清代在造办处设有专门作坊，清末民初随商号出口海外而声名远播。', feature: '以铜为胎、以丝为骨、以釉为衣，历经百余人次手工工序；纹样多用缠枝莲、宝相花等宫廷范式，蓝底金丝的配色极具辨识度。' },
  2: { history: '昆山腔经明代魏良辅改革形成"水磨腔"，梁辰鱼《浣纱记》首用昆腔搬演传奇，此后家班兴盛、传奇创作蔚为大观。', feature: '唱腔细腻婉转，讲究"啭音若丝"；行当分工严谨，载歌载舞，一唱三叹中蕴含严格的曲牌格律。' },
  3: { history: '蔚县剪纸源于明代窗户花装饰，清代随戏曲繁荣而以戏曲人物为主要题材，形成了"三分刻、七分染"的独特工艺体系。', feature: '以宣纸为料、刻刀代剪，阴刻主线条、阳刻主细节，再以品色点染，成品色彩浓艳而永不褪色。' },
  4: { history: '四大徽班进京后与汉调合流，吸收昆曲、秦腔养分，于道光年间形成京剧，此后随宫廷推崇与科班制度走向鼎盛。', feature: '唱念做打四功齐备，西皮二黄两大声腔系统；脸谱、行头、程式化动作构成完整的舞台符号体系。' },
  5: { history: '苏州自古蚕桑发达、画绣传统深厚，宋代苏州设绣局，明清时期"绣市"兴盛，形成了以精细雅洁为宗的流派风格。', feature: '针法几十种，讲究"平齐细密和光顺匀"八字诀；双面绣技艺可令正反两面同样完美，代表了中国刺绣的顶尖水准。' },
  6: { history: '龙泉窑始烧于三国两晋，宋代哥窑弟窑并立，梅子青与粉青釉色达到青瓷史巅峰，并向海外大量输出。', feature: '以当地紫金土与瓷石配胎、石灰碱釉多次施釉，经龙窑还原焰烧成；造型简练、釉色如玉。' },
  7: { history: '潍坊地处平原多风，清明放风筝习俗绵延数百年，郑板桥任职潍县时已有"纸花如雪满天飞"的记载。', feature: '扎、糊、绘、放四艺俱全，竹骨选三年毛竹、画面多取民间吉祥题材，龙头蜈蚣长数十米而一气呵成。' },
  8: { history: '古琴在先秦已是士人必修，唐宋出现斫琴名家与减字谱，明清琴派林立，形成了完整的琴学文献体系。', feature: '泛音、散音、按音三种音色象征天地人；记谱法沿用减字谱逾千年，曲目传承讲究打谱再创造。' },
  9: { history: '明末清初陈王廷在家传拳法基础上编创太极拳套路，其后陈氏传人将拳艺推向社会，衍生出杨、吴等诸流派。', feature: '以缠丝劲为核心，刚柔相济、内外兼修；套路与推手相辅相成，兼具技击、健身与文化修养价值。' },
  10: { history: '二十四节气在黄河流域的农耕实践中逐步完备，《淮南子》已见完整记载，历代颁行历法均以其为时间框架。', feature: '以太阳周年运动为依据划分，兼顾物候、农事与民俗，是典型的"天人合一"时间知识体系。' },
  11: { history: '苗族无文字，银饰承载着族群迁徙记忆与图腾信仰，锻制技艺在黔东南世代相传，成为苗族盛装不可分割的部分。', feature: '以纯银为料，经铸炼、锤打、拉丝、錾刻等工序制成；纹样中蝴蝶妈妈、牛角等母题蕴含族源叙事。' },
  12: { history: '德格印经院始建于清雍正七年，珍藏雕版二十余万块，整套传统印刷工艺得以完整延续至今。', feature: '从刻版、造纸到调墨印刷全流程手工完成，墨版朱版分色印刷，经版选材与晾晒工序极其讲究。' },
  13: { history: '非遗工坊是依托传统工艺项目设立的就业工坊，近年来在各地乡村振兴实践中快速发展。', feature: '以"传承人+农户"模式组织生产，让传统技艺转化为家门口的就业机会。' },
  14: { history: '非遗进校园始于本世纪初的试点探索，如今已成为非遗传播普及的重要渠道。', feature: '通过体验课、社团、校本课程等形式，让学生在动手实践中亲近传统文化。' },
  15: { history: '越剧起源于浙江嵊州的落地唱书，1906年登台演出后进入上海，吸收话剧、电影养分发展为女子越剧的黄金时代。', feature: '唱腔以尺调、四工调为主，表演真挚细腻，才子佳人题材居多，舞台唯美婉约。' },
  16: { history: '铜梁龙舞起于明代、盛于清代，扎龙与舞龙技艺代代相传，新中国成立后多次进京展演，享誉海内外。', feature: '火龙配以铁水打花，竞技龙强调套路的力与美；龙具扎制选竹讲究、彩绘精细。' },
  17: { history: '针灸理论基础见于《黄帝内经》，宋代针灸铜人开创经穴教学标准化先河，历代针灸典籍构成了完整知识体系。', feature: '以经络腧穴为纲，针刺讲究得气与补泻手法，艾灸借温热药力温通经络，适应症广泛。' },
  18: { history: '明末清初苏州书场林立，评弹艺人以一桌二椅说唱古今，随商业繁荣传播至长三角各市镇。', feature: '吴侬软语、说噱弹唱，琵琶三弦相和；一人多角、跳进跳出的叙事方式独具魅力。' },
  19: { history: '长调伴随游牧生活世代传唱，是蒙古族节庆、宴饮与日常抒情的重要方式，其潮尔唱法尤为古老。', feature: '旋律悠长、气息绵延，多用"诺古拉"颤音装饰；歌词多描绘草原、骏马与亲情。' },
  20: { history: '妈祖信仰自宋代湄洲发端，历代朝廷屡加敕封，随航海与移民传播至沿海各地及海外侨社。', feature: '祭典遵循古礼分春祭秋祭，信俗活动融合庙会、巡安等形式，体现立德行善大爱的精神内核。' },
  21: { history: '唐代泾县始产宣纸，宋元时期"泾县纸"名重一时，明清以来成为书画用纸的不二之选。', feature: '以青檀树皮与沙田稻草为料，历经百余道工序，成纸"韧而能润、光而不滑、久藏不腐"。' },
  22: { history: '《格萨尔》史诗大约形成于公元11世纪，在藏族民间以说唱形式流传千年，被誉为"东方的荷马史诗"。', feature: '活形态史诗仍在生长，神授艺人据称可通过梦授习得；说唱配合服饰、道具与曲调，规模浩瀚。' }
}

function makeContent(name: string, summary: string, originAge: string, region: string, notes?: { history: string; feature: string }): string {
  const history = notes?.history || `${name}发源可追溯至${originAge}，主要流传于${region}一带。在数百年的传承中，历代艺人代代相守、口传心授，形成了完整的技艺谱系与师承体系，是中华优秀传统文化的重要组成部分。`
  const feature = notes?.feature || `${name}在长期实践中形成了鲜明的地域特色与成熟的表现程式，兼具历史价值、艺术价值与科学价值。相关技艺与民俗深深融入当地民众的日常生活，成为地域文化的重要标识。`
  return [
    `<h3>项目概述</h3>`,
    `<p>${summary}</p>`,
    `<h3>历史渊源</h3>`,
    `<p>${history}</p>`,
    `<h3>主要特征</h3>`,
    `<p>${feature}</p>`,
    `<h3>传承现状</h3>`,
    `<p>近年来，在各级非遗保护机构与传承人的共同努力下，${name}通过名录保护、传习所建设、校园传播等方式焕发新的生机，公众认知度与社会参与度持续提升。</p>`
  ].join('')
}

interface SeedDecl {
  id: number
  name: string
  level: string
  categoryId: number
  region: string
  viewCount: number
  summary: string
  originAge: string
  distributionArea: string
  representativeWorks: string
  inheritor: string
  inheritorId: number | null
  endangerLevel: string
  isNews?: number
}

const seeds: SeedDecl[] = [
  { id: 1, name: '景泰蓝制作技艺', level: '国家级', categoryId: 8, region: '北京市', viewCount: 3260, summary: '景泰蓝又称铜胎掐丝珐琅，是一种在铜胎上以铜丝掐出图案、填施各色珐琅釉料烧制而成的宫廷工艺，因明代景泰年间盛行且釉色以蓝为主而得名。', originAge: '明代景泰年间', distributionArea: '北京市', representativeWorks: '铜胎掐丝珐琅瓶,珐琅炉', inheritor: '张同路', inheritorId: 1, endangerLevel: '需关注' },
  { id: 2, name: '昆曲', level: '国家级', categoryId: 4, region: '江苏省苏州市', viewCount: 5842, summary: '昆曲又称昆山腔，发源于元末明初的江苏昆山，被誉为"百戏之祖"，2001年被联合国教科文组织列入首批"人类口述和非物质遗产代表作"。', originAge: '元末明初', distributionArea: '江苏省、上海市、浙江省等地', representativeWorks: '牡丹亭,长生殿,桃花扇', inheritor: '陈雅韵', inheritorId: 2, endangerLevel: '需关注' },
  { id: 3, name: '中国剪纸（蔚县剪纸）', level: '国家级', categoryId: 7, region: '河北省张家口市', viewCount: 4108, summary: '蔚县剪纸以阴刻为主、阳刻为辅，重彩点染，是全国唯一以阴刻为主、点彩为辅的剪纸类型，被誉为"中华剪纸艺术之最"。', originAge: '清代道光年间', distributionArea: '河北省蔚县', representativeWorks: '戏曲脸谱,花鸟窗花', inheritor: '韩菊香', inheritorId: 5, endangerLevel: '良好' },
  { id: 4, name: '京剧', level: '国家级', categoryId: 4, region: '北京市', viewCount: 6630, summary: '京剧形成于清代道光年间的北京，融合徽调、汉调之长，腔调以西皮、二黄为主，被视为中国国粹，居中国戏曲三鼎甲之首。', originAge: '清代道光年间', distributionArea: '全国多地', representativeWorks: '贵妃醉酒,霸王别姬,定军山', inheritor: null, inheritorId: null, endangerLevel: '良好' },
  { id: 5, name: '苏绣', level: '国家级', categoryId: 7, region: '江苏省苏州市', viewCount: 3975, summary: '苏绣为中国四大名绣之一，以图案秀丽、构思巧妙、针法活泼、绣工精细著称，具有"平、齐、细、密、和、光、顺、匀"的特点。', originAge: '春秋时期', distributionArea: '江苏省苏州地区', representativeWorks: '双面绣猫,姑苏繁华图', inheritor: '沈凤霞', inheritorId: 3, endangerLevel: '良好' },
  { id: 6, name: '龙泉青瓷烧制技艺', level: '国家级', categoryId: 8, region: '浙江省龙泉市', viewCount: 2864, summary: '龙泉青瓷始于三国两晋，盛于宋元，以"青如玉、明如镜、声如磬"著称，是全球唯一入选人类非遗的陶瓷类项目。', originAge: '三国两晋时期', distributionArea: '浙江省丽水市龙泉地区', representativeWorks: '哥窑梅子青,弟窑粉青', inheritor: '王守拙', inheritorId: 4, endangerLevel: '需关注' },
  { id: 7, name: '潍坊风筝制作技艺', level: '国家级', categoryId: 8, region: '山东省潍坊市', viewCount: 2451, summary: '潍坊风筝兴于明代，盛于清代，选材讲究、造型优美、扎糊精巧、形象生动，潍坊因此被誉为"世界风筝之都"。', originAge: '明代', distributionArea: '山东省潍坊市', representativeWorks: '龙头蜈蚣,仙鹤童子', inheritor: null, inheritorId: null, endangerLevel: '良好' },
  { id: 8, name: '古琴艺术', level: '国家级', categoryId: 2, region: '北京市等地', viewCount: 3129, summary: '古琴是中国最早的弹拨乐器之一，已有三千年以上历史，2003年入选联合国教科文组织"人类口述和非物质遗产代表作"。', originAge: '上古时期', distributionArea: '全国多地', representativeWorks: '流水,广陵散,平沙落雁', inheritor: null, inheritorId: null, endangerLevel: '需关注' },
  { id: 9, name: '太极拳（陈氏太极拳）', level: '国家级', categoryId: 6, region: '河南省焦作市', viewCount: 5217, summary: '陈氏太极拳发源于明末清初的河南温县陈家沟，以刚柔相济、螺旋缠绕为特点，2020年列入联合国教科文组织人类非遗代表作名录。', originAge: '明末清初', distributionArea: '河南省温县及全国多地', representativeWorks: '老架一路,老架二路', inheritor: null, inheritorId: null, endangerLevel: '良好' },
  { id: 10, name: '二十四节气', level: '国家级', categoryId: 1, region: '全国', viewCount: 7306, summary: '二十四节气是中国人通过观察太阳周年运动而形成的时间知识体系，指导传统农业生产与日常生活，2016年入选联合国教科文组织人类非遗代表作名录。', originAge: '上古农耕时期', distributionArea: '黄河中下游地区及全国', representativeWorks: '立春,冬至,谷雨', inheritor: null, inheritorId: null, endangerLevel: '良好' },
  { id: 11, name: '苗族银饰锻制技艺', level: '国家级', categoryId: 8, region: '贵州省黔东南州', viewCount: 1938, summary: '苗族银饰锻制技艺以锤打、拉丝、掐丝、錾刻等工序制成，纹样承载苗族迁徙历史与图腾信仰，有"穿在身上的史诗"之称。', originAge: '明代', distributionArea: '贵州省黔东南苗族侗族自治州', representativeWorks: '银角,银冠,银项圈', inheritor: '欧阳松', inheritorId: 6, endangerLevel: '需关注' },
  { id: 12, name: '德格印经院藏族雕版印刷技艺', level: '国家级', categoryId: 8, region: '四川省甘孜州', viewCount: 1522, summary: '德格印经院雕版印刷技艺始于清雍正年间，从刻版、造纸到印刷全流程保持传统手工工艺，被誉为"雪山下的宝库"。', originAge: '清雍正年间', distributionArea: '四川省德格县', representativeWorks: '甘珠尔,丹珠尔经版', inheritor: null, inheritorId: null, endangerLevel: '濒危' },
  // 科普快讯类条目（列表页「科普快讯」筛选展示，同样可进详情页阅读）
  { id: 13, name: '全国非遗工坊建设现场会在苏州举行', level: '快讯', categoryId: 8, region: '江苏省苏州市', viewCount: 864, summary: '来自全国各地的非遗保护工作者齐聚苏州，围绕非遗工坊赋能乡村振兴展开交流研讨。', originAge: '—', distributionArea: '—', representativeWorks: '', inheritor: null, inheritorId: null, endangerLevel: '', isNews: 1 },
  { id: 14, name: '"非遗进校园"系列活动走进北京中小学', level: '快讯', categoryId: 4, region: '北京市', viewCount: 1023, summary: '京剧、剪纸等多个非遗项目传承人走进校园开设体验课，让学生近距离感受传统文化魅力。', originAge: '—', distributionArea: '—', representativeWorks: '', inheritor: null, inheritorId: null, endangerLevel: '', isNews: 1 },
  // —— 扩充批次（覆盖传统舞蹈/曲艺/传统医药/民俗/传统音乐/民间文学等类别） ——
  { id: 15, name: '越剧', level: '国家级', categoryId: 4, region: '浙江省嵊州市', viewCount: 2687, summary: '越剧发源于浙江嵊州，是中国第二大剧种，长于抒情，以唱腔优美、表演细腻著称，有"流传最广的地方剧种"之誉。', originAge: '清代光绪年间', distributionArea: '浙江省、上海市、江苏省等地', representativeWorks: '梁山伯与祝英台,红楼梦,祥林嫂', inheritor: null, inheritorId: null, endangerLevel: '良好' },
  { id: 16, name: '龙舞（铜梁龙舞）', level: '国家级', categoryId: 3, region: '重庆市铜梁区', viewCount: 2105, summary: '铜梁龙舞以龙具为道具的群体舞蹈，火龙沸腾、彩龙翻腾，被誉为"中华第一龙"，是巴渝民俗文化的杰出代表。', originAge: '明代', distributionArea: '重庆市铜梁区', representativeWorks: '火龙,竞技龙,夜光龙', inheritor: null, inheritorId: null, endangerLevel: '良好' },
  { id: 17, name: '中医针灸', level: '国家级', categoryId: 9, region: '全国多地', viewCount: 4520, summary: '针灸以针刺与艾灸防治疾病，理论基础是经络腧穴学说，2010年入选联合国教科文组织人类非遗代表作名录。', originAge: '先秦时期', distributionArea: '全国多地及海外华人社区', representativeWorks: '针灸铜人,黄帝内经', inheritor: null, inheritorId: null, endangerLevel: '良好' },
  { id: 18, name: '苏州评弹', level: '国家级', categoryId: 5, region: '江苏省苏州市', viewCount: 1876, summary: '苏州评弹是评话与弹词的合称，以吴侬软语说唱相间，一人多角、跳进跳出，被誉为"江南曲艺之花"。', originAge: '明代末年', distributionArea: '江苏省苏州市及长三角地区', representativeWorks: '珍珠塔,玉蜻蜓,描金凤', inheritor: null, inheritorId: null, endangerLevel: '需关注' },
  { id: 19, name: '蒙古族长调民歌', level: '国家级', categoryId: 2, region: '内蒙古自治区', viewCount: 1642, summary: '长调旋律悠长舒缓、意境开阔，是草原游牧文化的音乐结晶，2005年入选联合国教科文组织"人类口述和非物质遗产代表作"。', originAge: '蒙古汗国时期', distributionArea: '内蒙古自治区及蒙古国', representativeWorks: '辽阔的草原,小黄马', inheritor: null, inheritorId: null, endangerLevel: '濒危' },
  { id: 20, name: '妈祖祭典（妈祖信俗）', level: '国家级', categoryId: 10, region: '福建省莆田市', viewCount: 1989, summary: '妈祖信俗是以崇奉和颂扬妈祖的立德、行善、大爱精神为核心的民俗信俗，2009年入选联合国教科文组织人类非遗代表作名录。', originAge: '宋代', distributionArea: '福建省莆田市湄洲岛及沿海各地', representativeWorks: '春祭大典,秋祭大典,巡安绕境', inheritor: null, inheritorId: null, endangerLevel: '良好' },
  { id: 21, name: '宣纸制作技艺', level: '国家级', categoryId: 8, region: '安徽省泾县', viewCount: 1754, summary: '宣纸"轻似蝉翼白如雪，抖似细绸不闻声"，传统工艺共有百余道工序，被誉为"纸中之王"，是书画艺术的最佳载体。', originAge: '唐代', distributionArea: '安徽省宣城市泾县', representativeWorks: '生宣,熟宣,特净皮', inheritor: null, inheritorId: null, endangerLevel: '需关注' },
  { id: 22, name: '格萨尔', level: '国家级', categoryId: 1, region: '四川省甘孜州等地', viewCount: 1387, summary: '《格萨尔》是藏族人民集体创作的英雄史诗，有一百多万行、两千多万字，是世界上最长的活形态史诗，说唱艺人被称为"神授艺人"。', originAge: '公元11世纪前后', distributionArea: '西藏、四川、青海、甘肃等地藏族聚居区', representativeWorks: '格萨尔王传,霍岭大战', inheritor: null, inheritorId: null, endangerLevel: '濒危' }
]

/** 数据库：详情字段（正文 + 图集）在列表字段基础上补齐 */
export const heritageDb: HeritageItem[] = seeds.map((seed) => ({
  ...seed,
  isNews: seed.isNews ?? 0,
  categoryName: categories.find((c) => c.id === seed.categoryId)?.name ?? '',
  // 封面：优先取该项目第 1 张已就位的视觉中国素材（craft/cloisonne-01.webp 等），未就位退回本地占位图
  coverImage: coverFor(seed.id) || defaultCover(seed.name, 600, 360),
  publishTime: '2026-09-01 10:00:00',
  content: makeContent(seed.name, seed.summary, seed.originAge, seed.region, CULTURE_NOTES[seed.id]),
  // 图集：全量声明见 heritage-images.ts，素材放入即生效
  images: galleriesByHeritageId[seed.id] ?? []
}))

// ---------- 详情历史沿革（时间线） ----------
export function history(id: number): Array<{ year: string; event: string }> {
  const item = heritageDb.find((h) => h.id === id)
  if (!item) return []
  return [
    { year: item.originAge, event: `${item.name}在${item.region}一带发源成形。` },
    { year: '2006', event: `${item.name}入选第一批国家级非物质文化遗产名录。` },
    { year: '2026', event: `${item.name}纳入本平台数字化科普展示，面向公众传播。` }
  ]
}

// ---------- 列表分页查询（分类/级别/地区/关键词/快讯筛选） ----------
export function page(params: Record<string, unknown>): { records: HeritageItem[]; total: number } {
  const pageNum = Number(params.page) || 1
  const pageSize = Number(params.pageSize) || 8
  let list = heritageDb.slice()

  if (params.isNews === 1 || params.isNews === '1') list = list.filter((h) => h.isNews === 1)
  if (params.categoryId) list = list.filter((h) => h.categoryId === Number(params.categoryId))
  if (params.level) list = list.filter((h) => h.level === params.level)
  // 地区/关键词与真实后端一致：模糊匹配（LIKE %xx%）
  if (params.region) list = list.filter((h) => h.region.includes(String(params.region)))
  if (params.keyword) {
    const kw = String(params.keyword)
    list = list.filter((h) => h.name.includes(kw) || h.summary.includes(kw) || h.categoryName.includes(kw))
  }

  const total = list.length
  const records = list.slice((pageNum - 1) * pageSize, pageNum * pageSize)
  return { records, total }
}

// ---------- 详情 ----------
export function detail(id: number): HeritageItem | null {
  return heritageDb.find((h) => h.id === id) ?? null
}

// ---------- 相关推荐（同分类优先） ----------
export function recommend(heritageId?: number, limit = 4): HeritageItem[] {
  const current = heritageId ? heritageDb.find((h) => h.id === Number(heritageId)) : null
  const pool = current
    ? heritageDb.filter((h) => h.id !== current.id && h.categoryId === current.categoryId)
    : heritageDb.slice()
  const rest = heritageDb.filter((h) => h.id !== current?.id && !pool.includes(h))
  return [...pool, ...rest].slice(0, limit)
}

// ---------- 今日非遗冷知识 ----------
const tips = [
  '昆曲被称为"百戏之祖"，京剧、越剧等多个剧种都曾从昆曲中汲取养分。',
  '景泰蓝的制作要经过制胎、掐丝、点蓝、烧蓝、磨光、镀金等一百多道工序。',
  '二十四节气于2016年列入联合国教科文组织人类非遗代表作名录，被国际气象界誉为"中国的第五大发明"。',
  '蔚县剪纸不是"剪"出来的，而是先用刻刀刻制，再以点彩染色，是全国唯一以阴刻为主的剪纸。',
  '龙泉青瓷"弟窑"的粉青釉色，要经过1300℃左右高温还原烧成，成品青翠如玉。',
  '太极拳"以柔克刚"的理念源自中国传统哲学中的阴阳学说。',
  '越剧是中国唯一一个全部由女演员组成的专业剧种起家的剧种，早期"女班"曾风靡上海。',
  '铜梁火龙舞与铁水打花配合，1500℃的铁水抛向空中迸出"铁水流星"，场面被誉为"东方烟火"。',
  '中医针灸的"得气"指针刺入穴位后患者产生的酸、麻、胀、重感，是判断手法到位的重要标志。',
  '苏州评弹艺人说长篇书目可连讲数十天，"噱"是书中之宝，行业内有"说噱弹唱"四字诀。',
  '蒙古族长调的"诺古拉"是一种特殊的颤音装饰，被视为长调的灵魂。',
  '妈祖信俗随海上丝绸之路传播，目前全球妈祖宫庙上万座，信众超过三亿人。',
  '一张宣纸的制作要经历一百多道工序，从原料到成品历时近两年。',
  '《格萨尔》史诗有一百多万行，比世界五大史诗的总和还要长，且至今仍在被艺人传唱生长。'
]
let tipIndex = 0
export function randomTip(): { id: number; content: string } {
  tipIndex = (tipIndex + 1) % tips.length
  return { id: tipIndex, content: tips[tipIndex] }
}

// ---------- 科普快讯（管理员审核通过的用户发布内容合并展示） ----------
export function newsList(limit = 6): Array<Record<string, unknown>> {
  const official = heritageDb
    .filter((h) => h.isNews === 1)
    .slice(0, limit)
    .map((h) => ({
      id: h.id,
      title: h.name,
      content: h.summary,
      coverImage: h.coverImage,
      isTop: 1,
      heritageName: null,
      heritageId: h.id,
      publishTime: h.publishTime
    }))
  // 用户发布的快讯经审核通过后合并进来，按时间排序取前 limit 条
  return [...official, ...approvedNews()]
    .sort((a, b) => String(b.publishTime).localeCompare(String(a.publishTime)))
    .slice(0, limit)
}

// ---------- 公告 ----------
const notices = [
  { id: 1, title: '平台开放非遗知识在线测验功能公告', publishTime: '2026-09-20 09:00:00', content: '<p>为提升学习趣味性，平台上线"非遗小测验"功能，登录后即可参与答题并累计积分。</p>' },
  { id: 2, title: '关于非遗地图数据更新的说明', publishTime: '2026-09-15 14:30:00', content: '<p>本周已完成全国非遗点位数据核对更新，欢迎体验。</p>' },
  { id: 3, title: '毕业设计作品使用说明', publishTime: '2026-09-01 08:00:00', content: '<p>本平台为毕业设计作品，图片素材仅用于本地演示，非商用、不对外发布。</p>' }
]
export function noticePage(): { records: typeof notices; total: number } {
  return { records: notices, total: notices.length }
}
export function noticeDetail(id: number) {
  return notices.find((n) => n.id === Number(id)) ?? null
}

// ---------- 课程（科普视频，挂 B 站真实链接） ----------
/**
 * 视频来源说明（docs/16）：
 * - 含 BV 号的章节：详情页用 B 站官方播放器内嵌播放（CourseDetail 的 biliEmbed）；
 * - 无 BV 号的章节：videoUrl 填 B 站检索页链接，详情页提供"在新窗口打开观看"兜底；
 *   用户可自行替换为任意 B 站视频页链接（https://www.bilibili.com/video/BVxxxx）。
 * 站外视频仅供学习跳转，版权归属原上传者。
 */
function biliSearch(keyword: string): string {
  return `https://search.bilibili.com/all?keyword=${encodeURIComponent(keyword)}`
}

const courses = [
  {
    id: 1, name: '昆曲之美：水磨腔入门', heritageId: 2, cover: defaultCover('昆曲之美', 600, 360),
    teacher: '陈雅韵', duration: 45, viewCount: 2860, publishTime: '2026-08-20 10:00:00', chapterCount: 4,
    summary: '从曲牌、唱腔到身段，系统了解昆曲的审美体系。',
    chapters: [
      { videoUrl: 'https://www.bilibili.com/video/BV15W411J7Sm/' },
      { videoUrl: 'https://www.bilibili.com/video/BV1kr421G73L/' },
      { videoUrl: 'https://www.bilibili.com/video/BV1WS411w7Df/' },
      { videoUrl: biliSearch('昆曲 牡丹亭 选段') }
    ]
  },
  {
    id: 2, name: '景泰蓝的百年技艺', heritageId: 1, cover: defaultCover('景泰蓝技艺', 600, 360),
    teacher: '张同路', duration: 38, viewCount: 1954, publishTime: '2026-08-25 10:00:00', chapterCount: 3,
    summary: '跟拍景泰蓝制作全流程，看铜胎如何蜕变为珐琅重器。',
    chapters: [
      { videoUrl: biliSearch('景泰蓝 制作工艺') },
      { videoUrl: biliSearch('景泰蓝 纪录片') },
      { videoUrl: biliSearch('铜胎掐丝珐琅') }
    ]
  },
  {
    id: 3, name: '一针一线话苏绣', heritageId: 5, cover: defaultCover('苏绣', 600, 360),
    teacher: '沈凤霞', duration: 52, viewCount: 2310, publishTime: '2026-09-05 10:00:00', chapterCount: 5,
    summary: '平齐细密和光顺匀，八字诀里的苏绣密码。',
    chapters: [
      { videoUrl: biliSearch('苏绣 双面绣') },
      { videoUrl: biliSearch('苏绣 技术') },
      { videoUrl: biliSearch('刺绣 劈丝') },
      { videoUrl: biliSearch('苏绣 纪录片') },
      { videoUrl: biliSearch('绣娘 传承') }
    ]
  },
  {
    id: 4, name: '青瓷开窑：龙泉烧制实录', heritageId: 6, cover: defaultCover('龙泉青瓷', 600, 360),
    teacher: '王守拙', duration: 60, viewCount: 1580, publishTime: '2026-09-10 10:00:00', chapterCount: 3,
    summary: '从瓷土到青瓷，记录一次完整的开窑过程。',
    chapters: [
      { videoUrl: biliSearch('龙泉青瓷 烧制') },
      { videoUrl: biliSearch('青瓷 开窑') },
      { videoUrl: biliSearch('龙泉 窑工') }
    ]
  },
  // —— 扩充批次课程（覆盖新增类别，B 站检索链接，可替换为具体 BV 视频页） ——
  {
    id: 5, name: '越剧欣赏：从落地唱书到女子越剧', heritageId: 15, cover: defaultCover('越剧欣赏', 600, 360),
    teacher: '钱雅仙', duration: 48, viewCount: 1120, publishTime: '2026-09-18 10:00:00', chapterCount: 3,
    summary: '梳理越剧百年源流，欣赏经典剧目的唱腔与身段之美。',
    chapters: [
      { videoUrl: biliSearch('越剧 梁祝 选段') },
      { videoUrl: biliSearch('越剧 红楼梦 经典') },
      { videoUrl: biliSearch('越剧 纪录片') }
    ]
  },
  {
    id: 6, name: '书场里的江南：苏州评弹入门', heritageId: 18, cover: defaultCover('苏州评弹', 600, 360),
    teacher: '陆婉晴', duration: 40, viewCount: 860, publishTime: '2026-09-22 10:00:00', chapterCount: 3,
    summary: '听懂吴侬软语里的说噱弹唱，走进一桌二椅的江南书场。',
    chapters: [
      { videoUrl: biliSearch('苏州评弹 表演') },
      { videoUrl: biliSearch('评弹 琵琶 三弦') },
      { videoUrl: biliSearch('评弹 经典 弹词') }
    ]
  },
  {
    id: 7, name: '草原长调：游牧音乐的天籁', heritageId: 19, cover: defaultCover('蒙古族长调', 600, 360),
    teacher: '那日苏', duration: 36, viewCount: 745, publishTime: '2026-09-28 10:00:00', chapterCount: 3,
    summary: '在悠长旋律中理解游牧文明，认识长调的诺古拉与潮尔。',
    chapters: [
      { videoUrl: biliSearch('蒙古族长调 演唱') },
      { videoUrl: biliSearch('长调 那达慕') },
      { videoUrl: biliSearch('马头琴 长调') }
    ]
  },
  {
    id: 8, name: '针艾之间：中医针灸通识', heritageId: 17, cover: defaultCover('中医针灸', 600, 360),
    teacher: '程知远', duration: 55, viewCount: 1390, publishTime: '2026-10-08 10:00:00', chapterCount: 3,
    summary: '认识经络腧穴与针灸疗法，读懂"得气"背后的传统医学智慧。',
    chapters: [
      { videoUrl: biliSearch('中医针灸 教学') },
      { videoUrl: biliSearch('艾灸 科普') },
      { videoUrl: biliSearch('针灸 经络') }
    ]
  }
] as Array<Record<string, any>>

export function hotCourses(limit = 4) {
  // 官方课程 + 审核通过的用户发布视频，按发布时间倒序
  return [...courses.map(({ chapters, ...rest }) => ({ ...rest, chapterCount: chapters.length })),
          ...approvedCourses()]
    .sort((a, b) => String(b.publishTime).localeCompare(String(a.publishTime)))
    .slice(0, limit)
}
export function coursesByHeritage(heritageId?: number) {
  if (!heritageId) return []
  return [
    ...courses.filter((c) => c.heritageId === Number(heritageId)).map(({ chapters, ...rest }) => ({ ...rest, chapterCount: chapters.length })),
    ...approvedCourses().filter((c) => c.heritageId === Number(heritageId))
  ]
}
export function courseDetail(id: number) {
  const course = courses.find((c) => c.id === Number(id))
  if (course) {
    const { chapters, ...rest } = course
    return rest
  }
  // 用户发布的视频（审核通过）
  return approvedCourses().find((c) => c.id === Number(id)) ?? null
}
export function courseChapters(id: number) {
  const course = courses.find((c) => c.id === Number(id))
  if (course) {
    return course.chapters.map((ch, i) => ({
      id: i + 1,
      title: `${course.name.split('：')[0]} · 第 ${i + 1} 讲`,
      contentType: 'video',
      videoUrl: ch.videoUrl
    }))
  }
  // 用户发布的视频：单章节
  const userCourse = approvedCourses().find((c) => c.id === Number(id))
  if (!userCourse) return []
  return [{
    id: 1,
    title: userCourse.name,
    contentType: 'video',
    videoUrl: userCourse.chapters[0]?.videoUrl ?? ''
  }]
}

// ---------- 评论（内存态，可发新评论） ----------
interface CommentItem { id: number; heritageId: number; nickname: string; avatar: string; content: string; createTime: string }
let commentSeq = loadPersist<number>('commentSeq', 100)
const commentDb = loadPersist<Array<CommentItem & { heritageId: number }>>('comments', heritageDb.flatMap((h) => [{
  id: commentSeq++,
  heritageId: h.id,
  nickname: '非遗爱好者小林',
  avatar: '',
  content: `图文并茂，长知识了！${h.name}的介绍很细致。`,
  createTime: '2026-09-18 20:15:00'
}]))
function persistComments(): void {
  savePersist('commentSeq', commentSeq)
  savePersist('comments', commentDb)
}
export function commentPage(params: Record<string, unknown>) {
  const list = commentDb.filter((c) => c.heritageId === Number(params.heritageId)).slice().reverse()
  const pageNum = Number(params.page) || 1
  const pageSize = Number(params.pageSize) || 10
  return { records: list.slice((pageNum - 1) * pageSize, pageNum * pageSize), total: list.length }
}
export function addComment(data: { heritageId: number; content: string }, nickname: string): void {
  commentDb.push({
    id: commentSeq++,
    heritageId: Number(data.heritageId),
    nickname: nickname || '演示用户',
    avatar: '',
    content: data.content,
    createTime: new Date().toLocaleString('zh-CN', { hour12: false })
  })
  persistComments()
}

/** 我的评论（个人中心）：当前用户发出的评论，带项目名，含被屏蔽状态演示 */
export function myComments(nickname: string, params: Record<string, unknown>) {
  const list = commentDb
    .filter((c) => c.nickname === nickname)
    .slice()
    .reverse()
    .map((c) => ({
      ...c,
      heritageName: heritageDb.find((h) => h.id === c.heritageId)?.name ?? '',
      status: c.content.includes('屏蔽') ? 0 : 1
    }))
  const pageNum = Number(params.page) || 1
  const pageSize = Number(params.pageSize) || 10
  return { records: list.slice((pageNum - 1) * pageSize, pageNum * pageSize), total: list.length }
}

/** 删除我的评论（个人中心）：仅允许删除本人评论 */
export function deleteMyComment(id: number, nickname: string): boolean {
  const idx = commentDb.findIndex((c) => c.id === Number(id) && c.nickname === nickname)
  if (idx === -1) return false
  commentDb.splice(idx, 1)
  persistComments()
  return true
}

// ---------- mock 持久化工具（localStorage：mock 状态跨整页刷新保持，接近真实后端行为） ----------
function loadPersist<T>(key: string, fallback: T): T {
  try {
    const raw = localStorage.getItem(`heritage_mock_${key}`)
    return raw ? (JSON.parse(raw) as T) : fallback
  } catch {
    return fallback
  }
}
function savePersist(key: string, value: unknown): void {
  try {
    localStorage.setItem(`heritage_mock_${key}`, JSON.stringify(value))
  } catch { /* 隐私模式等场景静默降级为内存态 */ }
}

// ---------- 收藏（localStorage 持久化，字段与个人中心收藏列表对齐） ----------
const collectionStore = loadPersist<Array<{ heritageId: number; time: string }>>('collections', [])
export function myCollections(): Array<{ id: number; heritageId: number; name: string; level: string; region: string; coverImage: string; collectionTime: string }> {
  return collectionStore
    .map((c) => ({ c, h: heritageDb.find((h) => h.id === c.heritageId) }))
    .filter((x): x is { c: { heritageId: number; time: string }; h: HeritageItem } => !!x.h)
    .map(({ c, h }) => ({
      id: h.id,
      heritageId: h.id,
      name: h.name,
      level: h.level,
      region: h.region,
      coverImage: h.coverImage,
      collectionTime: c.time
    }))
}
export function addCollection(heritageId: number): void {
  const id = Number(heritageId)
  if (!collectionStore.some((c) => c.heritageId === id)) {
    collectionStore.push({ heritageId: id, time: new Date().toLocaleString('zh-CN', { hour12: false }) })
    savePersist('collections', collectionStore)
  }
}
export function removeCollection(heritageId: number): void {
  const idx = collectionStore.findIndex((c) => c.heritageId === Number(heritageId))
  if (idx !== -1) {
    collectionStore.splice(idx, 1)
    savePersist('collections', collectionStore)
  }
}

// ---------- 测验记录（localStorage 持久化） ----------
interface QuizRecord { id: number; questionId: number; answer: string; score: number; createTime: string }
const quizRecordDb = loadPersist<QuizRecord[]>('quizRecords', [])
let quizRecordSeq = loadPersist<number>('quizRecordSeq', 1)
export function saveQuizRecord(questionId: number, answer: string, score: number): void {
  quizRecordDb.push({
    id: quizRecordSeq++,
    questionId: Number(questionId),
    answer,
    score,
    createTime: new Date().toLocaleString('zh-CN', { hour12: false })
  })
  savePersist('quizRecordSeq', quizRecordSeq)
  savePersist('quizRecords', quizRecordDb)
}
export function quizRecords(): QuizRecord[] {
  return quizRecordDb.slice().reverse()
}
export function totalScore(): number {
  return quizRecordDb.reduce((sum, r) => sum + r.score, 0)
}

// ---------- 测验（题库 + 判分，内存态积分） ----------
interface QuizQuestion { id: number; score: number; question: string; optionA: string; optionB: string; optionC: string; optionD: string; correctAnswer: string }
const quizPool: QuizQuestion[] = [
  { id: 1, score: 10, question: '昆曲被誉为什么？', optionA: '百戏之祖', optionB: '国粹', optionC: '东方歌剧', optionD: '南戏之冠', correctAnswer: '百戏之祖' },
  { id: 2, score: 10, question: '景泰蓝的胎体通常用什么金属？', optionA: '铁', optionB: '铜', optionC: '银', optionD: '锡', correctAnswer: '铜' },
  { id: 3, score: 10, question: '二十四节气入选联合国教科文组织人类非遗名录是哪一年？', optionA: '2012年', optionB: '2016年', optionC: '2018年', optionD: '2020年', correctAnswer: '2016年' },
  { id: 4, score: 10, question: '蔚县剪纸的主要制作方式是？', optionA: '纯剪刀剪制', optionB: '刻刀刻制后点彩染色', optionC: '模版压制', optionD: '机刻手工上色', correctAnswer: '刻刀刻制后点彩染色' },
  { id: 5, score: 10, question: '苏绣的特点不包括以下哪项？', optionA: '平', optionB: '齐', optionC: '细', optionD: '粗', correctAnswer: '粗' },
  { id: 6, score: 10, question: '太极拳的发源地是？', optionA: '河南温县陈家沟', optionB: '湖北武当山', optionC: '四川青城山', optionD: '河北沧州', correctAnswer: '河南温县陈家沟' }
]
export function randomQuestions(heritageId?: number): QuizQuestion[] {
  // mock 阶段统一返回通用题库前 3 题（heritageId 仅保留参数占位）
  void heritageId
  return quizPool.slice(0, 3)
}
export function judgeAnswer(questionId: number, answer: string): { score: number; correct: boolean } {
  const q = quizPool.find((item) => item.id === Number(questionId))
  if (!q) return { score: 0, correct: false }
  const correct = q.correctAnswer === answer
  const score = correct ? q.score : 0
  saveQuizRecord(questionId, answer, score)
  return { score, correct }
}

// ---------- 学习进度（内存态：个人中心"我的观看" + 课程页进度上报） ----------
interface ProgressItem {
  id: number
  courseId: number
  courseName: string
  chapterId: number
  chapterTitle: string
  studyDuration: number
  finished: number
  updateTime: string
}
let progressSeq = loadPersist<number>('progressSeq', 1)
const progressDb = loadPersist<ProgressItem[]>('progress', [])
function persistProgress(): void {
  savePersist('progressSeq', progressSeq)
  savePersist('progress', progressDb)
}
function now(): string {
  return new Date().toLocaleString('zh-CN', { hour12: false })
}
/** 演示种子：昆曲课第 1 节已完成、第 2 节观看中 */
export function seedProgress(): void {
  if (progressDb.length) return
  upsertProgress({ courseId: 1, chapterId: 1, studyDuration: 12, finished: 1 })
  upsertProgress({ courseId: 1, chapterId: 2, studyDuration: 5, finished: 0 })
}
function upsertProgress(data: { courseId: number; chapterId: number; studyDuration?: number; finished?: number }): void {
  const courseId = Number(data.courseId)
  const chapterId = Number(data.chapterId)
  const courseName = courses.find((c) => c.id === courseId)?.name ?? ''
  const existing = progressDb.find((p) => p.courseId === courseId && p.chapterId === chapterId)
  const minutes = Number(data.studyDuration) || 0
  if (existing) {
    if (minutes > 0) existing.studyDuration += minutes
    if (data.finished) existing.finished = 1
    existing.updateTime = now()
    persistProgress()
    return
  }
  progressDb.push({
    id: progressSeq++,
    courseId,
    courseName,
    chapterId,
    chapterTitle: `${courseName}（第 ${chapterId} 节）`,
    studyDuration: minutes,
    finished: data.finished ? 1 : 0,
    updateTime: now()
  })
  persistProgress()
}
/** 我的学习记录；courseId 参数可选（课程页只取该课的记录） */
export function myProgress(courseId?: number): ProgressItem[] {
  seedProgress()
  const list = courseId ? progressDb.filter((p) => p.courseId === Number(courseId)) : progressDb
  return list.slice().sort((a, b) => b.updateTime.localeCompare(a.updateTime))
}
export function saveProgress(data: { courseId: number; chapterId: number; studyDuration?: number; finished?: number }): void {
  upsertProgress(data)
}
export function getLatest(): { courseId: number; chapterId: number; courseName: string; chapterTitle: string; studyDuration: number; finished: number } {
  seedProgress()
  const latest = myProgress()[0]
  return latest
    ? { courseId: latest.courseId, chapterId: latest.chapterId, courseName: latest.courseName, chapterTitle: latest.chapterTitle, studyDuration: latest.studyDuration, finished: latest.finished }
    : { courseId: 1, chapterId: 1, courseName: '昆曲之美：水磨腔入门', chapterTitle: '水磨腔的由来', studyDuration: 12, finished: 0 }
}
