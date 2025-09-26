import { drizzle } from 'drizzle-orm/mysql2';
import mysql from 'mysql2/promise';
import { config } from 'dotenv';
import * as path from 'path';
import { faker } from '@faker-js/faker';

// 참고: 일반적으로는 'src/schema'와 같이 소스 디렉토리에서 스키마를 관리하지만,
// 제공해주신 'drizzle/migrations/schema.ts' 파일을 기준으로 작성했습니다.
import * as schema from './drizzle/migrations/schema';

config({ path: path.resolve(__dirname, './.env') });

const { DATABASE_URL } = process.env;

// 생성할 데이터 수
const memberCount = 10000; // Member 테이블에 생성할 멤버 수
const onboardingCount = 10000; // Onboarding 테이블에 생성할 온보딩 수
const jobPostingCount = 80000; // JobPosting 테이블에 생성할 공고 수
const memoCount = 30000; // Memo 테이블에 생성할 메모 수
const jobRoleCount = 30000; // JobRole 테이블에 생성할 직무 수
const personalityCount = 10000; // Personality 테이블에 생성할 성향 수
const postingEvaluationCount = 20000; // PostingEvaluation 테이블에 생성할 평가 수
const scrapCount = 25000; // Scrap 테이블에 생성할 스크랩 수
const BATCH_SIZE = 1000; // 한 번에 삽입할 데이터 수

if (!DATABASE_URL) {
  throw new Error('DATABASE_URL이 .env 파일에 설정되지 않았습니다. ../../.env 파일을 확인해주세요.');
}

// 스키마의 enum 컬럼에서 랜덤 값을 가져오는 헬퍼 함수
const getRandomEnumValue = <T extends string>(enumValues: readonly [T, ...T[]]): T => {
    const randomIndex = Math.floor(Math.random() * enumValues.length);
    return enumValues[randomIndex];
};

const main = async () => {
  // 중요: DATABASE_URL이 'zighang_test' 데이터베이스를 가리키는지 확인하세요.
  const connection = await mysql.createConnection(DATABASE_URL);
  const db = drizzle(connection, { schema, mode: 'default' });

  // 사용자 요청사항 반영: 학교와 직무 데이터 제한
  const limitedSchools = ['SEOUL', 'KOREA', 'YEONSEI'] as const;
  const limitedJobRoles = ['백엔드 개발자', '프론트 개발자', '안드로이드 개발자', '임베디드개발자'] as const;

  console.log('데이터베이스 시딩을 시작합니다...');

  // 반복 가능한 시딩을 위해 기존 데이터를 삭제합니다.
  // 외래 키 제약 조건을 위반하지 않도록 자식 테이블부터 삭제합니다.
  console.log('기존 데이터를 삭제합니다...');
  await db.delete(schema.scrap);
  await db.delete(schema.memo);
  await db.delete(schema.postingEvaluation);
  await db.delete(schema.personality);
  await db.delete(schema.jobRole);
  await db.delete(schema.member);
  await db.delete(schema.jobPosting);
  await db.delete(schema.onboarding);

  // --- 1. Onboarding 데이터 시딩 ---
  // 다른 테이블에서 참조하므로 가장 먼저 생성합니다.
  console.log('Onboarding 데이터를 생성합니다...');
  const onboardingData: (typeof schema.onboarding.$inferInsert)[] = [];
  for (let i = 0; i < onboardingCount; i++) {
    onboardingData.push({
      createdAt: new Date(),
      updatedAt: new Date(),
      careerYear: getRandomEnumValue(schema.onboarding.careerYear.enumValues),
      jobCategory: getRandomEnumValue(limitedJobRoles),
      major: faker.person.jobType(),
      region: getRandomEnumValue(schema.onboarding.region.enumValues),
      school: getRandomEnumValue(limitedSchools),
    });
  }
  console.log(`Onboarding 데이터 ${onboardingData.length}개를 ${BATCH_SIZE}개씩 나눠서 삽입합니다...`);
  for (let i = 0; i < onboardingData.length; i += BATCH_SIZE) {
    const chunk = onboardingData.slice(i, i + BATCH_SIZE);
    await db.insert(schema.onboarding).values(chunk);
    process.stdout.write(`  ${i + chunk.length}/${onboardingData.length}개 삽입 완료\r`);
  }
  console.log('\nOnboarding 데이터 삽입 완료.');

  // 생성된 레코드를 가져와서 ID를 다른 테이블에서 사용합니다.
  const onboardings = await db.query.onboarding.findMany();

  // --- 1-1. JobRole 데이터 시딩 ---
  console.log('JobRole 데이터를 생성합니다...');
  const jobRoleData: (typeof schema.jobRole.$inferInsert)[] = [];
  if (onboardings.length > 0) {
      for (let i = 0; i < jobRoleCount; i++) {
          const randomOnboarding = onboardings[Math.floor(Math.random() * onboardings.length)];
          jobRoleData.push({
              createdAt: new Date(),
              updatedAt: new Date(),
              jobRole: getRandomEnumValue(limitedJobRoles),
              onboardingId: randomOnboarding.id,
          });
      }
      const uniqueJobRoleData = Array.from(new Map(jobRoleData.map(item => [`${item.jobRole}-${item.onboardingId}`, item])).values());
      console.log(`JobRole 데이터 ${uniqueJobRoleData.length}개를 ${BATCH_SIZE}개씩 나눠서 삽입합니다...`);
      for (let i = 0; i < uniqueJobRoleData.length; i += BATCH_SIZE) {
        const chunk = uniqueJobRoleData.slice(i, i + BATCH_SIZE);
        await db.insert(schema.jobRole).values(chunk);
        process.stdout.write(`  ${i + chunk.length}/${uniqueJobRoleData.length}개 삽입 완료\r`);
      }
      console.log('\nJobRole 데이터 삽입 완료.');
  }

  // --- 2. Member 데이터 시딩 ---
  console.log('Member 데이터를 생성합니다...');
  const memberData: (typeof schema.member.$inferInsert)[] = [];
  for (let i = 0; i < memberCount; i++) {
    memberData.push({
      createdAt: new Date(),
      updatedAt: new Date(),
      email: faker.internet.email({ firstName: `user${i}`}), // 중복 가능성을 줄이기 위함
      name: faker.person.fullName(),
      profileImageUrl: faker.image.avatar(),
      role: getRandomEnumValue(schema.member.role.enumValues),
      // 처음 10명의 멤버에게 onboarding ID를 할당합니다.
      onboardingId: i < onboardings.length ? onboardings[i].id : null,
    });
  }
  console.log(`Member 데이터 ${memberData.length}개를 ${BATCH_SIZE}개씩 나눠서 삽입합니다...`);
  for (let i = 0; i < memberData.length; i += BATCH_SIZE) {
    const chunk = memberData.slice(i, i + BATCH_SIZE);
    await db.insert(schema.member).values(chunk);
    process.stdout.write(`  ${i + chunk.length}/${memberData.length}개 삽입 완료\r`);
  }
  console.log('\nMember 데이터 삽입 완료.');

  const members = await db.query.member.findMany();

  // --- 2-1. Personality 데이터 시딩 ---
  // memberId가 고유해야 하므로, 생성된 member를 기반으로 만듭니다.
  console.log('Personality 데이터를 생성합니다...');
  const personalityData: (typeof schema.personality.$inferInsert)[] = [];
  if (members.length > 0) {
      for (const member of members) {
          if (personalityData.length >= personalityCount) break;
          personalityData.push({
              createdAt: new Date(),
              updatedAt: new Date(),
              memberId: member.id,
              charcterType: getRandomEnumValue(schema.personality.charcterType.enumValues),
              companySize: getRandomEnumValue(schema.personality.companySize.enumValues),
              companySizeValue: faker.number.int({ min: 1, max: 100 }),
              pursuitOfValueType: getRandomEnumValue(schema.personality.pursuitOfValueType.enumValues),
              pursuitOfValueTypeValue: faker.number.int({ min: 1, max: 100 }),
              workType: getRandomEnumValue(schema.personality.workType.enumValues),
              workTypeValue: faker.number.int({ min: 1, max: 100 }),
          });
      }
      console.log(`Personality 데이터 ${personalityData.length}개를 ${BATCH_SIZE}개씩 나눠서 삽입합니다...`);
      for (let i = 0; i < personalityData.length; i += BATCH_SIZE) {
        const chunk = personalityData.slice(i, i + BATCH_SIZE);
        await db.insert(schema.personality).values(chunk);
        process.stdout.write(`  ${i + chunk.length}/${personalityData.length}개 삽입 완료\r`);
      }
      console.log('\nPersonality 데이터 삽입 완료.');
  }


  // --- 3. JobPosting 데이터 시딩 ---
  // 이 테이블은 createdAt/updatedAt에 기본값이 설정되어 있어 직접 제공할 필요가 없습니다.
  console.log('JobPosting 데이터를 생성합니다...');
  const jobPostingData: (typeof schema.jobPosting.$inferInsert)[] = [];
  for (let i = 0; i < jobPostingCount; i++) {
    jobPostingData.push({
      company: JSON.stringify({
        recruiterUserId: null,
        companyImageUrl: faker.image.urlLoremFlickr({ category: 'business' }),
        companyRegion: null,
        companyType: 'ETC',
        recruiterEmail: null,
        companyName: faker.company.name(),
        companyDescription: null,
        companyAddress: null,
        businessNumber: null,
      }),
      title: faker.person.jobTitle(),
      content: faker.lorem.paragraphs(3),
      recruitmentRegion: faker.location.city(),
      career: `${faker.number.int({ min: 0, max: 5 })}-${faker.number.int({ min: 6, max: 10 })} years`,
      minCareer: faker.number.int({ min: 0, max: 5 }),
      maxCareer: faker.number.int({ min: 6, max: 10 }),
      applyCount: faker.number.int({ min: 0, max: 200 }),
      viewCount: faker.number.int({ min: 10, max: 5000 }),
      currentRank: faker.number.int({ min: 1, max: 100 }),
      lastRank: faker.number.int({ min: 1, max: 100 }),
      rankChange: getRandomEnumValue(schema.jobPosting.rankChange.enumValues),
      uploadDate: faker.date.past(),
    });
  }
  console.log(`JobPosting 데이터 ${jobPostingData.length}개를 ${BATCH_SIZE}개씩 나눠서 삽입합니다...`);
  for (let i = 0; i < jobPostingData.length; i += BATCH_SIZE) {
    const chunk = jobPostingData.slice(i, i + BATCH_SIZE);
    await db.insert(schema.jobPosting).values(chunk);
    process.stdout.write(`  ${i + chunk.length}/${jobPostingData.length}개 삽입 완료\r`);
  }
  console.log('\nJobPosting 데이터 삽입 완료.');

  const jobPostings = await db.query.jobPosting.findMany();


  // --- 4. Memo 데이터 시딩 ---
  // Memo는 Member와 JobPosting을 연결하며, 두 ID의 조합은 고유해야 합니다.
  console.log('Memo 데이터를 생성합니다...');
  const memoData: (typeof schema.memo.$inferInsert)[] = [];
  if (members.length > 0 && jobPostings.length > 0) {
      for (let i = 0; i < memoCount; i++) {
          const randomMember = members[Math.floor(Math.random() * members.length)];
          const randomPosting = jobPostings[Math.floor(Math.random() * jobPostings.length)];
          memoData.push({
              memberId: randomMember.id,
              postingId: randomPosting.id,
              memoContent: faker.lorem.sentence(),
          });
      }
      // (postingId, memberId) 복합 유니크 제약조건을 만족시키기 위해 중복을 제거합니다.
      const uniqueMemoData = Array.from(new Map(memoData.map(item => [`${item.postingId}-${item.memberId}`, item])).values());
      if (uniqueMemoData.length > 0) {
        console.log(`Memo 데이터 ${uniqueMemoData.length}개를 ${BATCH_SIZE}개씩 나눠서 삽입합니다...`);
        for (let i = 0; i < uniqueMemoData.length; i += BATCH_SIZE) {
          const chunk = uniqueMemoData.slice(i, i + BATCH_SIZE);
          await db.insert(schema.memo).values(chunk);
          process.stdout.write(`  ${i + chunk.length}/${uniqueMemoData.length}개 삽입 완료\r`);
        }
        console.log('\nMemo 데이터 삽입 완료.');
      }
  }

  // --- 5. PostingEvaluation 데이터 시딩 ---
  console.log('PostingEvaluation 데이터를 생성합니다...');
  const postingEvaluationData: (typeof schema.postingEvaluation.$inferInsert)[] = [];
  if (members.length > 0 && jobPostings.length > 0) {
      for (let i = 0; i < postingEvaluationCount; i++) {
          const randomMember = members[Math.floor(Math.random() * members.length)];
          const randomPosting = jobPostings[Math.floor(Math.random() * jobPostings.length)];
          postingEvaluationData.push({
              createdAt: new Date(),
              updatedAt: new Date(),
              memberId: randomMember.id,
              postingId: randomPosting.id,
              evalScore: faker.number.int({ min: 1, max: 5 }),
              evalText: faker.lorem.sentence(),
              recruitmentStep: getRandomEnumValue(schema.postingEvaluation.recruitmentStep.enumValues),
          });
      }
      const uniquePostingEvaluationData = Array.from(new Map(postingEvaluationData.map(item => [`${item.memberId}-${item.postingId}-${item.recruitmentStep}`, item])).values());
      console.log(`PostingEvaluation 데이터 ${uniquePostingEvaluationData.length}개를 ${BATCH_SIZE}개씩 나눠서 삽입합니다...`);
      for (let i = 0; i < uniquePostingEvaluationData.length; i += BATCH_SIZE) {
        const chunk = uniquePostingEvaluationData.slice(i, i + BATCH_SIZE);
        await db.insert(schema.postingEvaluation).values(chunk);
        process.stdout.write(`  ${i + chunk.length}/${uniquePostingEvaluationData.length}개 삽입 완료\r`);
      }
      console.log('\nPostingEvaluation 데이터 삽입 완료.');
  }

  // --- 6. Scrap 데이터 시딩 ---
  console.log('Scrap 데이터를 생성합니다...');
  const scrapData: (typeof schema.scrap.$inferInsert)[] = [];
  if (members.length > 0 && jobPostings.length > 0) {
      for (let i = 0; i < scrapCount; i++) {
          const randomMember = members[Math.floor(Math.random() * members.length)];
          const randomPosting = jobPostings[Math.floor(Math.random() * jobPostings.length)];
          scrapData.push({
              createdAt: new Date(),
              updatedAt: new Date(),
              memberId: randomMember.id,
              postingId: randomPosting.id,
              portfolioUrl: faker.internet.url(),
              resumeUrl: faker.internet.url(),
          });
      }
      const uniqueScrapData = Array.from(new Map(scrapData.map(item => [`${item.memberId}-${item.postingId}`, item])).values());
      console.log(`Scrap 데이터 ${uniqueScrapData.length}개를 ${BATCH_SIZE}개씩 나눠서 삽입합니다...`);
      for (let i = 0; i < uniqueScrapData.length; i += BATCH_SIZE) {
        const chunk = uniqueScrapData.slice(i, i + BATCH_SIZE);
        await db.insert(schema.scrap).values(chunk);
        process.stdout.write(`  ${i + chunk.length}/${uniqueScrapData.length}개 삽입 완료\r`);
      }
      console.log('\nScrap 데이터 삽입 완료.');
  }

  console.log('데이터베이스 시딩이 성공적으로 완료되었습니다!');

  await connection.end();
};

main().catch((err) => {
  console.error('데이터베이스 시딩 중 오류가 발생했습니다:', err);
  process.exit(1);
});