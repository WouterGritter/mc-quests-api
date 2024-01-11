package me.gritter.mcquests.api.queststate;

import java.util.Objects;

public class QuestProgress {

    private double total;

    public QuestProgress(double total) {
        if (total < 0.0) {
            throw new IllegalArgumentException("Total can't be negative.");
        }

        this.total = total;
    }

    public QuestProgress(int level, double progress) {
        if (level < 0) {
            throw new IllegalArgumentException("Level can't be negative.");
        }

        if (progress < 0.0 || progress >= 1.0) {
            throw new IllegalArgumentException("Progress has to satisfy 0.0 <= p < 1.0. Use updateProgress() to add an arbitrary amount of progress.");
        }

        this.total = level + progress;
    }

    public QuestProgress copy() {
        return new QuestProgress(this.total);
    }

    public double getTotalProgress() {
        return total;
    }

    public int getLevel() {
        return (int) total;
    }

    public double getProgress() {
        return total - (int) total;
    }

    public void setTotalProgress(double totalProgress) {
        if (totalProgress < 0.0) {
            throw new IllegalArgumentException("Total can't be negative.");
        }

        total = totalProgress;
    }

    public void setLevel(int level) {
        if (level < 0) {
            throw new IllegalArgumentException("Level can't be negative.");
        }

        total = level;
    }

    public void setProgress(double progress) {
        if (progress < 0.0 || progress >= 1.0) {
            throw new IllegalArgumentException("Progress has to satisfy 0.0 <= p < 1.0. Use updateProgress() to add an arbitrary amount of progress.");
        }

        total = (int) total + progress;
    }

    public void updateProgress(double progress) {
        total += progress;
        if (total < 0) {
            total = 0;
        }
    }

    @Override
    public String toString() {
        return "QuestProgress{" +
                "total=" + total +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestProgress that = (QuestProgress) o;
        return Double.compare(total, that.total) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(total);
    }
}
