// 定义数据初期类型

/*
用户类别
 */
// ？表示可选
export type UserType = {
    id: number;
    username: string;
    userAccount: string;
    avatarUrl?: string;
    profile?: string;
    gender: number;
    phone: string;
    email: string;
    userStatus: number;
    userRole: string;
    planetCode: string;
    tags: string[];
    createTime: Date;
};