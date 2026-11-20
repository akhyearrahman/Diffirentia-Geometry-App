package com.example.data

import androidx.compose.runtime.Immutable

@Immutable
data class LearningRule(
    val id: Int,
    val titleBn: String,
    val titleEn: String,
    val description: String
)

@Immutable
data class StudyModule(
    val id: String,
    val codeBn: String,
    val phaseIndex: Int,
    val titleBn: String,
    val titleEn: String,
    val geometricTakeaway: String,
    val isAvailable: Boolean = false
)

@Immutable
data class StudyPhase(
    val index: Int,
    val badgeBn: String,
    val titleBn: String,
    val titleEn: String,
    val subtitle: String,
    val modules: List<StudyModule>
)

@Immutable
data class AlignmentRow(
    val condition: String,
    val geometry: String,
    val shadowBehavior: String,
    val accentType: String // "positive", "zero", "negative"
)

@Immutable
data class IntuitionQuestion(
    val number: Int,
    val titleBn: String,
    val promptBn: String,
    val hintBn: String,
    val verifiedExplanationBn: String,
    val quickOptions: List<String>,
    val correctOptionIndex: Int
)

object StudyCurriculum {
    const val INTRO_BANNER_TEXT =
        "বিসিএসের দিনে ৮–১০ ঘণ্টার পড়াশোনার সাথে ভারসাম্য রেখে এই পুরো ১ বছরের যাত্রাটা সাজানো হয়েছে বই বা ভিডিওর বোঝা ছাড়াই—শুধু দৈনিক ৩০–৪৫ মিনিটের মাইক্রো-ক্লাসের জন্য। আমরা একেবারে মৌলিক স্কুল-কলেজের অঙ্ক থেকে নয়, বরং আন্ডারগ্র্যাজুয়েট গণিতের ধারণাগুলোর গভীর জ্যামিতিক অনুভূতি (Geometric Intuition) তৈরি করা থেকে শুরু করে সরাসরি রিসার্চ লেভেল পর্যন্ত যাব।"

    const val FOOTER_NOTE_TEXT =
        "বিসিএসের রুটিনের ফাঁকে যখনই সময় পাবে, মডিউল নম্বর বেছে নিয়ে ঠিক সেই জায়গা থেকেই শুরু করো—কোনো তাড়াহুড়ো নেই, তবে প্রতিটি ধাপ হবে গভীর ও নিখুঁত।"

    val learningRules = listOf(
        LearningRule(
            id = 1,
            titleBn = "আগে ছবি, পরে সমীকরণ",
            titleEn = "Geometry First, Algebra Second",
            description = "যেকোনো টপিক—তা ডট গুণন (Dot Product) হোক বা ম্যানিফোল্ড (Manifold)—শুরুতে মনের চোখে এর জ্যামিতিক চিত্র তৈরি করব, তারপর দেখব সেই চিত্রটা কীভাবে সমীকরণে প্রকাশ পায়।"
        ),
        LearningRule(
            id = 2,
            titleBn = "প্রতিটি পরিভাষার ইংরেজি রূপ",
            titleEn = "Bilingual Research Terminology",
            description = "রিসার্চ পেপার পড়ার অভ্যস্ততা তৈরি করতে প্রতিটি বাংলা গাণিতিক পরিভাষার পাশে ব্র্যাকেটে তার ইংরেজি নাম থাকবে।"
        ),
        LearningRule(
            id = 3,
            titleBn = "এক বসাতে একটি মডিউল",
            titleEn = "Micro-Modules (30–45 Min)",
            description = "প্রতিটি সেশন ছোট ও পরিপাকযোগ্য হবে, যাতে বিসিএসের পড়ার মনোযোগ নষ্ট না হয়।"
        ),
        LearningRule(
            id = 4,
            titleBn = "জ্যামিতিক যাচাই",
            titleEn = "Intuition Check",
            description = "প্রতিটি মডিউল শেষে ১–২টি ছোট জ্যামিতিক প্রশ্ন থাকবে। ধারণাটি পুরোপুরি পরিষ্কার হলেই আমরা পরের ধাপে এগোব।"
        )
    )

    val phases: List<StudyPhase> = listOf(
        StudyPhase(
            index = 1,
            badgeBn = "ধাপ ১ • মাস ১–২",
            titleBn = "জ্যামিতিক ভিত্তি পুনর্গঠন",
            titleEn = "Rebuilding Geometric Intuition",
            subtitle = "ক্যালকুলাস ও লিনিয়ার অ্যালজেব্রার পরিচিত সূত্রগুলোকে ফর্মুলা হিসেবে নয়, বরং স্পেস (Space) ও আকৃতির (Shape) ভাষা হিসেবে নতুন করে দেখা।",
            modules = listOf(
                StudyModule(
                    id = "1.1",
                    codeBn = "১.১",
                    phaseIndex = 1,
                    titleBn = "ভেক্টর, ডট গুণন ও ইনার প্রোডাক্ট",
                    titleEn = "Vectors, Dot Product & Inner Product",
                    geometricTakeaway = "ডট গুণন কীভাবে লম্ব অভিক্ষেপ (Orthogonal Projection), দৈর্ঘ্য ও কোণ মাপার মৌলিক স্কেল তৈরি করে",
                    isAvailable = true
                ),
                StudyModule(
                    id = "1.2",
                    codeBn = "১.২",
                    phaseIndex = 1,
                    titleBn = "ক্রস গুণন, ডিটারমিন্যান্ট ও লিনিয়ার ট্রান্সফরমেশন",
                    titleEn = "Cross Product, Determinant & Linear Transformations",
                    geometricTakeaway = "ম্যাট্রিক্স কীভাবে স্পেসকে টেনে লম্বা করে, ঘোরায় বা চেপ্টা করে এবং ক্ষেত্রফল-আয়তন বদলে দেয়"
                ),
                StudyModule(
                    id = "1.3",
                    codeBn = "১.৩",
                    phaseIndex = 1,
                    titleBn = "আইগেনভ্যালু, আইগেনভেক্টর ও সিমেট্রিক ম্যাট্রিক্স",
                    titleEn = "Eigenvalues, Eigenvectors & Symmetric Matrices",
                    geometricTakeaway = "স্পেস বিকৃত হওয়ার সময় কোন অক্ষগুলো দিক পরিবর্তন করে না—যা পরে বক্রতার মূল অক্ষ হয়ে উঠবে"
                ),
                StudyModule(
                    id = "1.4",
                    codeBn = "১.৪",
                    phaseIndex = 1,
                    titleBn = "মাল্টিভেরিয়েবল ক্যালকুলাসের জ্যামিতি",
                    titleEn = "Geometry of Multivariable Calculus",
                    geometricTakeaway = "গ্র্যাডিয়েন্ট (Gradient), ডিরেকশনাল ডেরিভেটিভ (Directional Derivative) ও জ্যাকোবিয়ান ম্যাট্রিক্স (Jacobian Matrix)-এর মাধ্যমে বক্র ফাংশনকে স্থানীয়ভাবে সমতল (Local Linearization) করে দেখা"
                )
            )
        ),
        StudyPhase(
            index = 2,
            badgeBn = "ধাপ ২ • মাস ৩–৫",
            titleBn = "ক্লাসিক্যাল ডিফারেনশিয়াল জিওমেট্রি",
            titleEn = "Classical Differential Geometry in ℝ³",
            subtitle = "আমাদের পরিচিত ত্রিমাত্রিক জগতে (ℝ³) বক্ররেখা (Curves) ও বক্রতল (Surfaces) বিশ্লেষণ করা—এখান থেকেই আসল ডিফারেনশিয়াল জিওমেট্রির স্বাদ পাওয়া শুরু হবে।",
            modules = listOf(
                StudyModule(
                    id = "2.1",
                    codeBn = "২.১",
                    phaseIndex = 2,
                    titleBn = "ত্রিমাত্রিক বক্ররেখা ও চলমান ফ্রেম",
                    titleEn = "Space Curves & Frenet-Serret Frame",
                    geometricTakeaway = "আর্ক-লেংথ (Arc-Length), বক্রতা (Curvature, κ) এবং মোচড় বা টর্শন (Torsion, τ)—একটি রোলার-কোস্টারের গতির দৃষ্টিতে রেখা বোঝা"
                ),
                StudyModule(
                    id = "2.2",
                    codeBn = "২.২",
                    phaseIndex = 2,
                    titleBn = "রেগুলার সারফেস ও স্পর্শক তল",
                    titleEn = "Regular Surfaces & Tangent Plane",
                    geometricTakeaway = "প্যারামেট্রাইজেশন (Parametrization), স্পর্শক তল (Tangent Space) এবং লম্ব ভেক্টর (Normal Vector) তৈরি করা"
                ),
                StudyModule(
                    id = "2.3",
                    codeBn = "২.৩",
                    phaseIndex = 2,
                    titleBn = "ফার্স্ট ফান্ডামেন্টাল ফর্ম",
                    titleEn = "First Fundamental Form",
                    geometricTakeaway = "বাঁকা তলের ওপর বসবাসকারী একটি পিঁপড়ের দৃষ্টিতে দূরত্ব, কোণ ও ক্ষেত্রফল মাপার কৌশল"
                ),
                StudyModule(
                    id = "2.4",
                    codeBn = "২.৪",
                    phaseIndex = 2,
                    titleBn = "সেকেন্ড ফান্ডামেন্টাল ফর্ম ও শেপ অপারেটর",
                    titleEn = "Second Fundamental Form & Shape Operator",
                    geometricTakeaway = "বাইরে থেকে দেখলে তলটি কীভাবে বেঁকে যাচ্ছে এবং নরমাল ভেক্টর কীভাবে ঘুরছে তা মাপা"
                ),
                StudyModule(
                    id = "2.5",
                    codeBn = "২.৫",
                    phaseIndex = 2,
                    titleBn = "বক্রতার প্রকারভেদ",
                    titleEn = "Principal, Gaussian & Mean Curvatures",
                    geometricTakeaway = "প্রধান বক্রতা (κ₁, κ₂), গাউসিয়ান বক্রতা (Gaussian Curvature, K), গড় বক্রতা (Mean Curvature, H) এবং সাবানের বুদবুদের মতো মিনিমাল সারফেস (Minimal Surfaces)"
                ),
                StudyModule(
                    id = "2.6",
                    codeBn = "২.৬",
                    phaseIndex = 2,
                    titleBn = "গাউসের মহা-উপপাদ্য, জিওডেসিক ও গাউস-বনে",
                    titleEn = "Theorema Egregium, Geodesics & Gauss-Bonnet Theorem",
                    geometricTakeaway = "অন্তর্নিহিত বক্রতা (Intrinsic Curvature), বাঁকা তলের সবচেয়ে সোজা পথ বা জিওডেসিক (Geodesic) এবং জ্যামিতির সাথে টপোলজির (Topology) গভীর সম্পর্ক"
                )
            )
        ),
        StudyPhase(
            index = 3,
            badgeBn = "ধাপ ৩ • মাস ৬–৯",
            titleBn = "আধুনিক ম্যানিফোল্ড, টেন্সর ও রিম্যানিয়ান জিওমেট্রি",
            titleEn = "Manifolds, Tensors & Riemannian Geometry",
            subtitle = "ত্রিমাত্রিক জগতের সীমাবদ্ধতা ভেঙে যেকোনো মাত্রার (n-dimensional) বক্র জগতের আধুনিক ভাষা আয়ত্ত করা—রিসার্চ পেপার পড়ার আসল চাবিকাঠি।",
            modules = listOf(
                StudyModule(
                    id = "3.1",
                    codeBn = "৩.১",
                    phaseIndex = 3,
                    titleBn = "প্রয়োজনীয় টপোলজি",
                    titleEn = "Essential Point-Set Topology",
                    geometricTakeaway = "দূরত্ব ছাড়াই \"কাছাকাছি থাকা\" বোঝা: ওপেন সেট (Open Set), হোমিওমরফিজম (Homeomorphism) ও কম্প্যাক্টনেস (Compactness)"
                ),
                StudyModule(
                    id = "3.2",
                    codeBn = "৩.২",
                    phaseIndex = 3,
                    titleBn = "স্মুথ ম্যানিফোল্ড",
                    titleEn = "Smooth Manifolds",
                    geometricTakeaway = "পৃথিবীর মানচিত্রের অ্যাটলাসের মতো চার্ট (Chart) ও অ্যাটলাস (Atlas) জোড়া লাগিয়ে বাইরের স্পেস ছাড়াই বক্র জগৎ নির্মাণ"
                ),
                StudyModule(
                    id = "3.3",
                    codeBn = "৩.৩",
                    phaseIndex = 3,
                    titleBn = "ট্যানজেন্ট স্পেস, কো-ট্যানজেন্ট স্পেস ও ভেক্টর ফিল্ড",
                    titleEn = "Tangent/Cotangent Spaces & Vector Fields",
                    geometricTakeaway = "বক্র জগতের প্রতিটি বিন্দুতে আলাদা রৈখিক স্পেস; ডেরিভেটিভকে ভেক্টর হিসেবে দেখা; পুশফরওয়ার্ড (Pushforward) ও পুলব্যাক (Pullback)"
                ),
                StudyModule(
                    id = "3.4",
                    codeBn = "৩.৪",
                    phaseIndex = 3,
                    titleBn = "ডিফারেনশিয়াল ফর্ম ও স্টোকস উপপাদ্য",
                    titleEn = "Differential Forms & Generalized Stokes' Theorem",
                    geometricTakeaway = "ওয়েজ প্রোডাক্ট (Wedge Product), এক্সটেরিয়র ডেরিভেটিভ (Exterior Derivative) এবং উচ্চমাত্রার আয়তন পরিমাপের নিখুঁত যন্ত্র"
                ),
                StudyModule(
                    id = "3.5",
                    codeBn = "৩.৫",
                    phaseIndex = 3,
                    titleBn = "টেন্সর ও মেট্রিক টেন্সর",
                    titleEn = "Tensors & Metric Tensor (gᵢⱼ)",
                    geometricTakeaway = "টেন্সরকে কেবল সূচক (Indices) হিসেবে নয়, বরং জ্যামিতিক মেশিন (Multilinear Map) হিসেবে বোঝা; কোভ্যারিয়েন্ট (Covariant) বনাম কন্ট্রাভ্যারিয়েন্ট (Contravariant)"
                ),
                StudyModule(
                    id = "3.6",
                    codeBn = "৩.৬",
                    phaseIndex = 3,
                    titleBn = "কানেকশন, প্যারালাল ট্রান্সপোর্ট ও ক্রিস্টোফেল সিম্বল",
                    titleEn = "Levi-Civita Connection, Parallel Transport & Christoffel Symbols (Γᵏᵢⱼ)",
                    geometricTakeaway = "বক্র ম্যানিফোল্ডে এক বিন্দু থেকে আরেক বিন্দুতে ভেক্টরকে না ঘুরিয়ে সরানো এবং কোভ্যারিয়েন্ট ডেরিভেটিভ (Covariant Derivative)"
                ),
                StudyModule(
                    id = "3.7",
                    codeBn = "৩.৭",
                    phaseIndex = 3,
                    titleBn = "রিম্যান, রিচি ও স্কেলার বক্রতা",
                    titleEn = "Riemann, Ricci & Scalar Curvature Tensors",
                    geometricTakeaway = "বদ্ধ পথে প্যারালাল ট্রান্সপোর্ট করলে ভেক্টর কতটুকু ঘুরে যায়—তা থেকেই উচ্চমাত্রার পূর্ণাঙ্গ বক্রতা মাপা"
                )
            )
        ),
        StudyPhase(
            index = 4,
            badgeBn = "ধাপ ৪ • মাস ১০–১২",
            titleBn = "রিসার্চ স্পেশালাইজেশন ও প্রথম পেপার",
            titleEn = "Research Specialization & First Paper",
            subtitle = "অর্জিত জ্ঞানকে বাস্তব গবেষণায় রূপান্তর করা।",
            modules = listOf(
                StudyModule(
                    id = "4.1",
                    codeBn = "৪.১",
                    phaseIndex = 4,
                    titleBn = "রিসার্চ শাখা নির্বাচন",
                    titleEn = "Choosing a Research Niche",
                    geometricTakeaway = "সাবম্যানিফোল্ড থিওরি (Submanifold Theory), ইনফরমেশন জিওমেট্রি (Information Geometry), ডিসক্রিট ডিফারেনশিয়াল জিওমেট্রি (Discrete Differential Geometry) অথবা লরেন্টজিয়ান জিওমেট্রি (Lorentzian Geometry)-র মধ্যে ১টি বেছে নেওয়া"
                ),
                StudyModule(
                    id = "4.2",
                    codeBn = "৪.২",
                    phaseIndex = 4,
                    titleBn = "জার্নাল পেপার ব্যবচ্ছেদ",
                    titleEn = "Paper Dissection",
                    geometricTakeaway = "সাম্প্রতিক ২–৩টি রিসার্চ পেপার ভেঙে বোঝা এবং সেগুলোর সীমাবদ্ধতা (Limitations) শনাক্ত করা"
                ),
                StudyModule(
                    id = "4.3",
                    codeBn = "৪.৩",
                    phaseIndex = 4,
                    titleBn = "রিসার্চ গ্যাপ ও গাণিতিক সমাধান",
                    titleEn = "Problem Solving & Verification",
                    geometricTakeaway = "একটি সুনির্দিষ্ট প্রবলেম স্টেটমেন্ট (Problem Statement) নিয়ে প্রমাণ (Proof) বা গণনা দাঁড় করানো এবং সিম্বলিক টুল (Python/SymPy) দিয়ে ফলাফল যাচাই করা"
                ),
                StudyModule(
                    id = "4.4",
                    codeBn = "৪.৪",
                    phaseIndex = 4,
                    titleBn = "ম্যানুস্ক্রিপ্ট ড্রাফটিং",
                    titleEn = "LaTeX Writing & Submission Prep",
                    geometricTakeaway = "আন্তর্জাতিক মানের কাঠামোতে পেপারটি LaTeX-এ সাজানো ও সঠিক জার্নাল নির্বাচন করা"
                )
            )
        )
    )

    val allModules: List<StudyModule> = phases.flatMap { it.modules }

    val module11AlignmentRows = listOf(
        AlignmentRow(
            condition = "u⃗ · v⃗ > 0",
            geometry = "সূক্ষ্মকোণ (Acute, θ < 90°)",
            shadowBehavior = "u⃗-এর ছায়া v⃗-এর একই দিকে পড়ে",
            accentType = "positive"
        ),
        AlignmentRow(
            condition = "u⃗ · v⃗ = 0",
            geometry = "লম্ব (Orthogonal, θ = 90°)",
            shadowBehavior = "u⃗-এর কোনো ছায়াই পড়ে না (শূন্য সহযোগিতা)",
            accentType = "zero"
        ),
        AlignmentRow(
            condition = "u⃗ · v⃗ < 0",
            geometry = "স্থূলকোণ (Obtuse, θ > 90°)",
            shadowBehavior = "u⃗-এর ছায়া v⃗-এর বিপরীত দিকে পড়ে",
            accentType = "negative"
        )
    )

    val module11Questions = listOf(
        IntuitionQuestion(
            number = 1,
            titleBn = "ছায়া বনাম ডট গুণন (Shadow vs. Dot Product)",
            promptBn = "ধরো দুটি অশূন্য ভেক্টর u⃗ এবং v⃗-এর মধ্যবর্তী কোণ 60°। এখন v⃗-এর দিক একই রেখে তার দৈর্ঘ্য দ্বিগুণ করে দেওয়া হলো। এতে (ক) v⃗-এর রেখার ওপর u⃗-এর ছায়ার দৈর্ঘ্য (Orthogonal Projection) কি বদলাবে? এবং (খ) ডট গুণন u⃗ · v⃗-এর মান কীভাবে বদলাবে?",
            hintBn = "ছায়ার দৈর্ঘ্য = ‖u⃗‖ cos θ — এখানে কি ‖v⃗‖ আছে? আর ডট গুণন = (ছায়ার দৈর্ঘ্য) × ‖v⃗‖!",
            verifiedExplanationBn = "নিখুঁত জ্যামিতিক উত্তর:\n(ক) ছায়ার দৈর্ঘ্য (‖u⃗‖ cos 60°) একদমই বদলাবে না! কারণ v⃗-এর রেখার দিক একই আছে এবং u⃗ বদলায়নি।\n(খ) ডট গুণন u⃗ · v⃗ = (‖u⃗‖ cos θ) × ‖v⃗‖ হওয়ায়, ‖v⃗‖ দ্বিগুণ করলে ডট গুণনের মান ঠিক দ্বিগুণ (2×) হয়ে যাবে! ভেক্টর ল্যাবে 'Q1: 2×v⃗' বাটন চেপে এটি সরাসরি চোখে দেখতে পারো।",
            quickOptions = listOf(
                "ছায়ার দৈর্ঘ্য অপরিবর্তিত থাকবে; ডট গুণন দ্বিগুণ হবে",
                "ছায়ার দৈর্ঘ্য দ্বিগুণ হবে; ডট গুণন চারগুণ হবে",
                "ছায়ার দৈর্ঘ্য অর্ধেক হবে; ডট গুণন অপরিবর্তিত থাকবে"
            ),
            correctOptionIndex = 0
        ),
        IntuitionQuestion(
            number = 2,
            titleBn = "নতুন স্কেলের জগৎ (Metric Tensor Preview)",
            promptBn = "ধরো আমরা ℝ² সমতলে নতুন একটি ইনার প্রোডাক্ট সংজ্ঞায়িত করলাম G = [[4, 0], [0, 1]] দিয়ে, অর্থাৎ ⟨u⃗, v⃗⟩ = 4u₁v₁ + u₂v₂। এই নতুন স্কেল অনুযায়ী (1, 0) ভেক্টরটির দৈর্ঘ্য কত হবে, আর (0, 1) ভেক্টরটির দৈর্ঘ্য কত হবে? আর এই জগতে যেসব ভেক্টরের দৈর্ঘ্য ঠিক 1 (⟨v⃗, v⃗⟩ = 1), সেগুলোকে সাধারণ গ্রাফ পেপারে আঁকলে দেখতে বৃত্ত (Circle) হবে নাকি অন্য কোনো আকৃতি?",
            hintBn = "‖v⃗‖_G = √(4v₁² + v₂²)। এবার ⟨v⃗, v⃗⟩ = 1 অর্থাৎ 4x² + y² = 1 সমীকরণটি গ্রাফ পেপারে কী আঁকে?",
            verifiedExplanationBn = "অসাধারণ!\n• (1, 0)-এর নতুন দৈর্ঘ্য = √(4·1² + 0) = 2 একক।\n• (0, 1)-এর নতুন দৈর্ঘ্য = √(0 + 1²) = 1 একক।\n• আর ⟨v⃗, v⃗⟩ = 1 মানে 4x² + y² = 1 বা (x / 0.5)² + (y / 1)² = 1 — যা সাধারণ গ্রাফ পেপারে আঁকলে একটি উপবৃত্ত (Ellipse) হবে, যার x-অক্ষ বরাবর অর্ধ-অক্ষ মাত্র 0.5 এবং y-অক্ষ বরাবর 1! নিচের মেট্রিক ক্যানভাসে 'G = diag(4,1)' প্রিসেট চেপে এটি সরাসরি দেখো।",
            quickOptions = listOf(
                "‖(1,0)‖=2, ‖(0,1)‖=1; একক দৈর্ঘ্যের বিন্দুগুলো একটি উপবৃত্ত (Ellipse) আঁকবে",
                "‖(1,0)‖=4, ‖(0,1)‖=1; একক দৈর্ঘ্যের বিন্দুগুলো একটি বৃত্ত (Circle) আঁকবে",
                "‖(1,0)‖=0.5, ‖(0,1)‖=1; একক দৈর্ঘ্যের বিন্দুগুলো একটি পরাবৃত্ত (Parabola) আঁকবে"
            ),
            correctOptionIndex = 0
        )
    )
}
